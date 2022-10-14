package db.migration;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type V 2 __ generate flights data.
 */
public class V2__GenerateFlightsData extends BaseJavaMigration {


    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));
        generateAirports(jdbcTemplate);
        generateFlights(jdbcTemplate);
    }

    private void generateAirports(JdbcTemplate jdbcTemplate) throws IOException {
        Map<String, String> airports = readAirportInfo();
        AtomicInteger count = new AtomicInteger();
        airports.forEach((code, coordinatesString) -> {
            //procees only 1000 airports except LED and DXB
            if(count.get()<10000|| StringUtils.equals(code, "DXB")||StringUtils.equals(code, "LED")) {
                if (StringUtils.isNotEmpty(coordinatesString)) {
                    String[] coordinates = coordinatesString.split(",");
                    if (NumberUtils.isCreatable(coordinates[0].trim()) && NumberUtils.isCreatable(coordinates[1].trim())) {
                        BigDecimal longitude = new BigDecimal(coordinates[0].trim());
                        BigDecimal latitude = new BigDecimal(coordinates[1].trim());
                        count.incrementAndGet();
                        jdbcTemplate.update("insert into airports(airport_code, longtitude, latitude) "
                                + "values(?, ?, ?)", code, longitude, latitude);
                    }
                }
            }
        });
    }

    private void generateFlights(JdbcTemplate jdbcTemplate) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from airports", Integer.class);
        Integer dxb = jdbcTemplate.queryForObject("select id from airports where airport_code='DXB'", Integer.class);
        List<LocalDate> dates = getDatesForAYear();
        for (LocalDate date : dates) {
            for (int i = 1; i < count; i++) {
                if (i != dxb) {
                    jdbcTemplate.update("insert into flights(departure_id, destination_id, flight_number, flight_date) "
                            + "values(?, ?, ?, ?)", i, dxb, "FLIGHT_" + i + "_" + dxb + "_" + date, date);
                    jdbcTemplate.update("insert into flights(departure_id, destination_id, flight_number, flight_date) "
                            + "values(?, ?, ?, ?)", dxb, i, "FLIGHT_" + dxb + "_" + i + "_" + date, date);
                }
            }
        }
    }

    private List<LocalDate> getDatesForAYear() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.parse("2023-01-01");
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end))
                .collect(Collectors.toList());
    }

    private Map<String, String> readAirportInfo() throws IOException {
        Map<String, String> result = new HashMap<>();
        FileReader filereader = new FileReader(ResourceUtils.getFile("classpath:datasets/airport-codes.csv"));
        CSVReader csvReader = new CSVReader(filereader);
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            if (StringUtils.isNotEmpty(nextRecord[nextRecord.length - 3])) {
                result.put(nextRecord[nextRecord.length - 3], nextRecord[nextRecord.length - 1]);
            }
        }
        return result;
    }
}
