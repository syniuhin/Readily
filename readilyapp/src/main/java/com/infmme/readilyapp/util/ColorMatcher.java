package com.infmme.readilyapp.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created with love, by infm dated on 6/17/16.
 */

public class ColorMatcher {

  private static String[] sMaterialHexes = new String[] {
/*
      "B71C1C", "880E4F", "4A148C", "311B92", "1A237E", "0D47A1", "01579B",
      "006064", "004D40", "1B5E20", "33691E", "827717", "F57F17", "FF6F00",
      "E65100", "BF360C", "3E2723", "212121", "263238",
*/
      "EF5350", "F06292", "BA68C8", "9575CD", "7986CB", "1E88E5", "039BE5",
      "0097A7", "009688", "43A047", "689F38", "827717", "EF6C00", "FF5722",
      "8D6E63", "757575", "607D8B"
  };

  // Full set of colors
  private static String[] sMaterialRgbs = new String[] { "244 67 54", "255 " +
      "235 238", "255 205 210", "239 154 154", "229 115 115", "239 83 80",
      "229 57 53", "211 47 47", "198 40 40", "183 28 28", "255 138 128", "255" +
      " 82 82", "255 23 68", "213 0 0", "233 30 99", "252 228 236", "248 187 " +
      "208", "244 143 177", "240 98 146", "236 64 122", "216 27 96", "194 24 " +
      "91", "173 20 87", "136 14 79", "255 128 171", "255 64 129", "245 0 " +
      "87", "197 17 98", "156 39 176", "243 229 245", "225 190 231", "206 147" +
      " 216", "186 104 200", "171 71 188", "142 36 170", "123 31 162", "106 " +
      "27 154", "74 20 140", "234 128 252", "224 64 251", "213 0 249", "170 0" +
      " 255", "103 58 183", "237 231 246", "209 196 233", "179 157 219", "149" +
      " 117 205", "126 87 194", "94 53 177", "81 45 168", "69 39 160", "49 27" +
      " 146", "179 136 255", "124 77 255", "101 31 255", "98 0 234", "63 81 " +
      "181", "232 234 246", "197 202 233", "159 168 218", "121 134 203", "92 " +
      "107 192", "57 73 171", "48 63 159", "40 53 147", "26 35 126", "140 158" +
      " 255", "83 109 254", "61 90 254", "48 79 254", "33 150 243", "227 242 " +
      "253", "187 222 251", "144 202 249", "100 181 246", "66 165 245", "30 " +
      "136 229", "25 118 210", "21 101 192", "13 71 161", "130 177 255", "68 " +
      "138 255", "41 121 255", "41 98 255", "3 169 244", "225 245 254", "179 " +
      "229 252", "129 212 250", "79 195 247", "41 182 246", "3 155 229", "2 " +
      "136 209", "2 119 189", "1 87 155", "128 216 255", "64 196 255", "0 176" +
      " 255", "0 145 234", "0 188 212", "224 247 250", "178 235 242", "128 " +
      "222 234", "77 208 225", "38 198 218", "0 172 193", "0 151 167", "0 131" +
      " 143", "0 96 100", "132 255 255", "24 255 255", "0 229 255", "0 184 " +
      "212", "0 150 136", "224 242 241", "178 223 219", "128 203 196", "77 " +
      "182 172", "38 166 154", "0 137 123", "0 121 107", "0 105 92", "0 77 " +
      "64", "167 255 235", "100 255 218", "29 233 182", "0 191 165", "76 175 " +
      "80", "232 245 233", "200 230 201", "165 214 167", "129 199 132", "102 " +
      "187 106", "67 160 71", "56 142 60", "46 125 50", "27 94 32", "185 246 " +
      "202", "105 240 174", "0 230 118", "0 200 83", "139 195 74", "241 248 " +
      "233", "220 237 200", "197 225 165", "174 213 129", "156 204 101", "124" +
      " 179 66", "104 159 56", "85 139 47", "51 105 30", "204 255 144", "178 " +
      "255 89", "118 255 3", "100 221 23", "205 220 57", "249 251 231", "240 " +
      "244 195", "230 238 156", "220 231 117", "212 225 87", "192 202 51",
      "175 180 43", "158 157 36", "130 119 23", "244 255 129", "238 255 65",
      "198 255 0", "174 234 0", "255 235 59", "255 253 231", "255 249 196",
      "255 245 157", "255 241 118", "255 238 88", "253 216 53", "251 192 45",
      "249 168 37", "245 127 23", "255 255 141", "255 255 0", "255 234 0",
      "255 214 0", "255 193 7", "255 248 225", "255 236 179", "255 224 130",
      "255 213 79", "255 202 40", "255 179 0", "255 160 0", "255 143 0", "255" +
      " 111 0", "255 229 127", "255 215 64", "255 196 0", "255 171 0", "255 " +
      "152 0", "255 243 224", "255 224 178", "255 204 128", "255 183 77",
      "255 167 38", "251 140 0", "245 124 0", "239 108 0", "230 81 0", "255 " +
      "209 128", "255 171 64", "255 145 0", "255 109 0", "255 87 34", "251 " +
      "233 231", "255 204 188", "255 171 145", "255 138 101", "255 112 67",
      "244 81 30", "230 74 25", "216 67 21", "191 54 12", "255 158 128", "255" +
      " 110 64", "255 61 0", "221 44 0", "121 85 72", "239 235 233", "215 204" +
      " 200", "188 170 164", "161 136 127", "141 110 99", "109 76 65", "93 64" +
      " 55", "78 52 46", "62 39 35", "158 158 158", "250 250 250", "245 245 " +
      "245", "238 238 238", "224 224 224", "189 189 189", "117 117 117", "97 " +
      "97 97", "66 66 66", "33 33 33", "96 125 139", "236 239 241", "207 216 " +
      "220", "176 190 197", "144 164 174", "120 144 156", "84 110 122", "69 " +
      "90 100", "55 71 79", "38 50 56", "0 0 0", "255 255 255" };


  public static int[] meanColor(final Bitmap bitmap) {
    return meanColor(bitmap, 1 << 4);
  }

  public static int[] meanColor(final Bitmap bitmap, int darkOffset) {
    int height = bitmap.getHeight();
    int width = bitmap.getWidth();
    int[] pixels = new int[height * width];
    bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
                     bitmap.getHeight());
    int[] pixelSum = new int[] {
        darkOffset, darkOffset, darkOffset, darkOffset
    };
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int[] argb = new int[4];
        colorToArgb(pixels[i * width + j], argb);
        for (int k = 0; k < 4; ++k) {
          pixelSum[k] += argb[k];
        }
      }
    }

    return new int[] {
        pixelSum[0] / (height * width),
        pixelSum[1] / (height * width),
        pixelSum[2] / (height * width),
        pixelSum[3] / (height * width)
    };
  }

  public static int pickRandomMaterialColor() {
    Random r = new Random();
    int rgbPacked = Integer.parseInt(
        sMaterialHexes[r.nextInt(sMaterialHexes.length)], 16);
    int[] rgb = new int[4];
    colorToArgb(rgbPacked, rgb);
    return Color.argb(0xF8, rgb[1], rgb[2], rgb[3]);
  }

  public static int findClosestMaterialColor(Bitmap bitmap) {
    return findClosestMaterialColor(meanColor(bitmap));
  }

  public static int findClosestMaterialColor(int[] argb) {
    int minDistance = 1 << 30;
    int minDistanceIndex = -1;
/*
    for (int i = 0; i < sMaterialRgbs.length; ++i) {
      String material = sMaterialRgbs[i];
      String[] rgb = material.split(" ");
      int currentDistance = Math.abs(Integer.parseInt(rgb[0]) - argb[1]) +
          Math.abs(Integer.parseInt(rgb[1]) - argb[2]) +
          Math.abs(Integer.parseInt(rgb[2]) - argb[3]);
      if (minDistance > currentDistance) {
        minDistance = currentDistance;
        minDistanceIndex = i;
      }
    }
*/
    for (int i = 0; i < sMaterialHexes.length; ++i) {
      String materialHex = sMaterialHexes[i];
      int rgbPacked = Integer.parseInt(materialHex, 16);
      int[] rgb = new int[4];
      colorToArgb(rgbPacked, rgb);
      int currentDistance = Math.abs(rgb[1] - argb[1]) +
          Math.abs(rgb[2] - argb[2]) + Math.abs(rgb[3] - argb[3]);
      if (minDistance > currentDistance) {
        minDistance = currentDistance;
        minDistanceIndex = i;
      }
    }
/*
    String[] closestMatch = sMaterialRgbs[minDistanceIndex].split(" ");
*/
    int rgbPacked = Integer.parseInt(sMaterialHexes[minDistanceIndex], 16);
    int[] rgb = new int[4];
    colorToArgb(rgbPacked, rgb);
/*
    return Color.argb(255, Integer.parseInt(closestMatch[0]),
                      Integer.parseInt(closestMatch[1]),
                      Integer.parseInt(closestMatch[2]));
*/
    return Color.argb(255, rgb[1], rgb[2], rgb[3]);
  }

  private static void colorToArgb(int color, int[] argb) {
    argb[0] = color >> 24 & 0xFF;
    argb[1] = (color >> 16) & 0xFF;
    argb[2] = (color >> 8) & 0xFF;
    argb[3] = color & 0xFF;
  }
}