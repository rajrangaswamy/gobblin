package gobblin.source.extractor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * Each {@link gobblin.source.workunit.WorkUnit} has a corresponding {@link WatermarkInterval}. The
 * {@link WatermarkInterval} represents the range of the data that needs to be pulled for the {@link WorkUnit}. So, the
 * {@link gobblin.source.workunit.WorkUnit} should pull data from the {@link #lowWatermark} to the
 * {@link #expectedHighWatermark}.
 */
public class WatermarkInterval {

  public static final String LOW_WATERMARK_TO_JSON_KEY = "low.watermark.to.json";
  public static final String EXPECTED_HIGH_WATERMARK_TO_JSON_KEY = "expected.watermark.to.json";

  private final Watermark lowWatermark;
  private final Watermark expectedHighWatermark;

  public WatermarkInterval(Watermark lowWatermark, Watermark expectedHighWatermark) {
    this.lowWatermark = lowWatermark;
    this.expectedHighWatermark = expectedHighWatermark;
  }

  public Watermark getLowWatermark() {
    return this.lowWatermark;
  }

  public Watermark getExpectedHighWatermark() {
    return this.expectedHighWatermark;
  }

  public JsonElement toJson() {
    JsonObject jsonObject = new JsonObject();

    jsonObject.add(LOW_WATERMARK_TO_JSON_KEY, this.lowWatermark.toJson());
    jsonObject.add(EXPECTED_HIGH_WATERMARK_TO_JSON_KEY, this.expectedHighWatermark.toJson());

    return jsonObject;
  }
}
