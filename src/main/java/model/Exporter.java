package model;

import java.io.IOException;

public interface Exporter {

  String export(ICalendarModel model, String outputPath) throws IOException;

}
