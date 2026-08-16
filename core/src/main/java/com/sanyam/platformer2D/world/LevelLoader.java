package com.sanyam.platformer2D.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    private static final int WALL_VALUE = 1;
    private static final int PLATFORM_VALUE = 2;

    public static class LevelData {
        public List<Rectangle> solids = new ArrayList<>();
        public List<Rectangle> platforms = new ArrayList<>();
        public int pixelWidth;
        public int pixelHeight;
    }

    // A horizontal run within a single row: columns [colStart, colEnd) share the same value.
    private static class Interval {
        int colStart, colEnd, value;
        Interval(int colStart, int colEnd, int value) {
            this.colStart = colStart; this.colEnd = colEnd; this.value = value;
        }
    }

    // A rectangle being grown across multiple rows, while its column range keeps matching.
    private static class OpenRect {
        int colStart, colEnd, value, rowStart, rowEndExclusive;
        OpenRect(Interval interval, int rowStart) {
            this.colStart = interval.colStart; this.colEnd = interval.colEnd; this.value = interval.value;
            this.rowStart = rowStart; this.rowEndExclusive = rowStart + 1;
        }
        boolean matches(Interval interval) {
            return colStart == interval.colStart && colEnd == interval.colEnd && value == interval.value;
        }
    }

    public static LevelData load(String internalPath) {
        JsonValue root = new JsonReader().parse(Gdx.files.internal(internalPath));

        JsonValue level = root.get("levels").get(0);
        int levelPxHei = level.getInt("pxHei");

        JsonValue intGridLayer = findIntGridLayer(level);
        int cWid = intGridLayer.getInt("__cWid");
        int cHei = intGridLayer.getInt("__cHei");
        int gridSize = intGridLayer.getInt("__gridSize");
        JsonValue csv = intGridLayer.get("intGridCsv");

        LevelData data = new LevelData();
        data.pixelWidth = level.getInt("pxWid");
        data.pixelHeight = levelPxHei;

        List<OpenRect> active = new ArrayList<>();

        for (int cy = 0; cy < cHei; cy++) {
            List<Interval> rowIntervals = computeRowIntervals(csv, cy, cWid);
            boolean[] consumed = new boolean[rowIntervals.size()];

            List<OpenRect> stillActive = new ArrayList<>();

            // Try to extend each currently-open rectangle with a matching interval in this row.
            for (OpenRect openRect : active) {
                boolean extended = false;
                for (int i = 0; i < rowIntervals.size(); i++) {
                    if (!consumed[i] && openRect.matches(rowIntervals.get(i))) {
                        openRect.rowEndExclusive = cy + 1;
                        consumed[i] = true;
                        extended = true;
                        break;
                    }
                }
                if (extended) {
                    stillActive.add(openRect);
                } else {
                    // This rectangle didn't continue into this row — it's finished, emit it.
                    emitRectangle(openRect, gridSize, levelPxHei, data);
                }
            }

            // Any interval in this row not matched to an existing open rectangle starts a new one.
            for (int i = 0; i < rowIntervals.size(); i++) {
                if (!consumed[i]) {
                    stillActive.add(new OpenRect(rowIntervals.get(i), cy));
                }
            }

            active = stillActive;
        }

        // Close out anything still open after the last row.
        for (OpenRect openRect : active) {
            emitRectangle(openRect, gridSize, levelPxHei, data);
        }

        return data;
    }

    private static List<Interval> computeRowIntervals(JsonValue csv, int cy, int cWid) {
        List<Interval> intervals = new ArrayList<>();
        int runStart = -1;
        int runValue = 0;

        for (int cx = 0; cx <= cWid; cx++) {
            int value = (cx < cWid) ? csv.get(cy * cWid + cx).asInt() : -1;

            if (value != runValue) {
                if (runValue != 0) {
                    intervals.add(new Interval(runStart, cx, runValue));
                }
                runStart = cx;
                runValue = value;
            }
        }
        return intervals;
    }

    private static void emitRectangle(OpenRect openRect, int gridSize, int levelPxHei, LevelData data) {
        float worldX = openRect.colStart * gridSize;
        float bottomYLdtk = openRect.rowEndExclusive * gridSize; // lowest edge, in LDtk's y-down space
        float worldY = levelPxHei - bottomYLdtk;                  // flipped to y-up
        float width = (openRect.colEnd - openRect.colStart) * gridSize;
        float height = (openRect.rowEndExclusive - openRect.rowStart) * gridSize;

        Rectangle rect = new Rectangle(worldX, worldY, width, height);

        if (openRect.value == WALL_VALUE) {
            data.solids.add(rect);
        } else if (openRect.value == PLATFORM_VALUE) {
            data.platforms.add(rect);
        }
    }

    private static JsonValue findIntGridLayer(JsonValue level) {
        for (JsonValue layer : level.get("layerInstances")) {
            if ("IntGrid".equals(layer.getString("__type"))) {
                return layer;
            }
        }
        throw new RuntimeException("No IntGrid layer found in level.");
    }
}
