package com.p3.archon.coreprocess.executables.tools.utility.common;

public final class Color
{

  public static Color white = new Color(255, 255, 255);

  public static Color fromHSV(final float hue,
                              final float saturation,
                              final float value)
  {
    final float normaliedHue = hue - (float) Math.floor(hue);
    final int h = (int) (normaliedHue * 6);
    final float f = normaliedHue * 6 - h;
    final float p = value * (1 - saturation);
    final float q = value * (1 - f * saturation);
    final float t = value * (1 - (1 - f) * saturation);

    switch (h)
    {
      case 0:
        return rgbToString(value, t, p);
      case 1:
        return rgbToString(q, value, p);
      case 2:
        return rgbToString(p, value, t);
      case 3:
        return rgbToString(p, q, value);
      case 4:
        return rgbToString(t, p, value);
      case 5:
        return rgbToString(value, p, q);
      default:
        throw new RuntimeException(String
          .format("Could not convert from HSV (%f, %f, %f) to RGB",
                  normaliedHue,
                  saturation,
                  value));
    }
  }

  public static Color rgbToString(final float r, final float g, final float b)
  {
    return new Color((int) (r * 255 + 0.5),
                     (int) (g * 255 + 0.5),
                     (int) (b * 255 + 0.5));
  }

  private final int r;
  private final int g;
  private final int b;

  private Color(final int r, final int g, final int b)
  {
    if (r < 0)
    {
      this.r = 0;
    }
    else if (r > 255)
    {
      this.r = 255;
    }
    else
    {
      this.r = r;
    }

    if (g < 0)
    {
      this.g = 0;
    }
    else if (g > 255)
    {
      this.g = 255;
    }
    else
    {
      this.g = g;
    }

    if (b < 0)
    {
      this.b = 0;
    }
    else if (b > 255)
    {
      this.b = 255;
    }
    else
    {
      this.b = b;
    }
  }

  @Override
  public String toString()
  {
    final int rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;

    final String htmlColor = "#" + Integer.toHexString(rgb).toUpperCase();
    return htmlColor;
  }

}
