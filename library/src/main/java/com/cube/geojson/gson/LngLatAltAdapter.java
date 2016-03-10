package com.cube.geojson.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.cube.geojson.LngLatAlt;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringTokenizer;

/**
 * LngLatAlt de-serialization in Gson
 */
public class LngLatAltAdapter implements JsonSerializer<LngLatAlt>, JsonDeserializer<LngLatAlt> {

    @Override
    public JsonElement serialize(LngLatAlt src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(src.getLongitude()));
        array.add(new JsonPrimitive(src.getLatitude()));
        if (src.hasAltitude()) {
            array.add(new JsonPrimitive(src.getAltitude()));
        }
        return array;
    }

    @Override
    public LngLatAlt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray data = json.getAsJsonArray();

        LngLatAlt node = new LngLatAlt();

        double longitude = getCoordinate(data.get(0).getAsDouble());
        double latitude = getCoordinate(data.get(1).getAsDouble());

        node.setLongitude(longitude);
        node.setLatitude(latitude);

        if (data.size() == 3) {
            node.setAltitude(data.get(2).getAsDouble());
        } else {
            node.setAltitude(Double.NaN);
        }

        return node;
    }

    private double getCoordinate(double value) {
        double decLat = Math.abs(value) / 36000;
        double minLat = Math.abs(decLat % 1) * 60;
        double secLat = Math.abs(minLat % 1) * 60;

        String resultLat = (Math.signum(value) == -1 ? "-" : "") + new BigDecimal(decLat).setScale(2, RoundingMode.HALF_UP).doubleValue() + ":" + new BigDecimal(minLat).setScale(2, RoundingMode.HALF_UP).doubleValue() + ":" + new BigDecimal(secLat).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return convert(resultLat);
    }

    public static double convert(String coordinate) {
        // IllegalArgumentException if bad syntax
        if (coordinate == null) {
            throw new NullPointerException("coordinate");
        }

        boolean negative = false;
        if (coordinate.charAt(0) == '-') {
            coordinate = coordinate.substring(1);
            negative = true;
        }

        StringTokenizer st = new StringTokenizer(coordinate, ":");
        int tokens = st.countTokens();
        if (tokens < 1) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        try {
            String degrees = st.nextToken();
            double val;
            if (tokens == 1) {
                val = Double.parseDouble(degrees);
                return negative ? -val : val;
            }

            String minutes = st.nextToken();
            int deg = Integer.parseInt(degrees);
            double min;
            double sec = 0.0;

            if (st.hasMoreTokens()) {
                min = Integer.parseInt(minutes);
                String seconds = st.nextToken();
                sec = Double.parseDouble(seconds);
            } else {
                min = Double.parseDouble(minutes);
            }

            boolean isNegative180 = negative && (deg == 180) &&
                    (min == 0) && (sec == 0);

            // deg must be in [0, 179] except for the case of -180 degrees
            if ((deg < 0.0) || (deg > 179 && !isNegative180)) {
                throw new IllegalArgumentException("coordinate=" + coordinate);
            }
            if (min < 0 || min > 59) {
                throw new IllegalArgumentException("coordinate=" +
                        coordinate);
            }
            if (sec < 0 || sec > 59) {
                throw new IllegalArgumentException("coordinate=" +
                        coordinate);
            }

            val = deg * 3600.0 + min * 60.0 + sec;
            val /= 3600.0;
            return negative ? -val : val;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
    }
}
