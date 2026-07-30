package com.ayurveda.attendance.service.impl;

import com.ayurveda.attendance.config.AdmsProperties;
import com.ayurveda.attendance.service.AdmsDeviceService;
import com.ayurveda.attendance.service.DeviceAttendanceLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdmsDeviceServiceImpl implements AdmsDeviceService {

    private static final String TABLE_ATTLOG = "ATTLOG";

    private final AdmsProperties admsProperties;
    private final DeviceAttendanceLogService deviceAttendanceLogService;

    @Override
    public boolean isDeviceAllowed(String serialNumber) {
        if (CollectionUtils.isEmpty(admsProperties.getAllowedSerialNumbers())) {
            return true;
        }
        if (!StringUtils.hasText(serialNumber)) {
            return false;
        }
        return admsProperties.getAllowedSerialNumbers().stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(serialNumber.trim()));
    }

    @Override
    public String buildOptionsResponse(String serialNumber) {
        String sn = StringUtils.hasText(serialNumber) ? serialNumber.trim() : "";
        return """
                GET OPTION FROM: %s
                Stamp=9999
                OpStamp=9999
                ErrorDelay=60
                Delay=30
                TransTimes=00:00;14:00
                TransInterval=1
                TransFlag=1111000000
                TimeZone=5.5
                Realtime=1
                Encrypt=0
                """.formatted(sn).trim() + "\n";
    }

    @Override
    public void processCdataPost(String serialNumber, String table, String rawBody) {
        if (!StringUtils.hasText(rawBody)) {
            log.debug("Empty ADMS POST body from SN={}", serialNumber);
            return;
        }

        String normalizedTable = table != null ? table.trim().toUpperCase() : "";
        if (StringUtils.hasText(normalizedTable) && !TABLE_ATTLOG.equals(normalizedTable)) {
            log.info("Ignoring non-ATTLOG ADMS table={} from SN={}", normalizedTable, serialNumber);
            return;
        }

        String[] lines = rawBody.split("\\r?\\n");
        for (String line : lines) {
            parseAndSavePunchLine(serialNumber, normalizedTable.isEmpty() ? TABLE_ATTLOG : normalizedTable, line);
        }
    }

    private void parseAndSavePunchLine(String serialNumber, String table, String line) {
        line = line != null ? line.trim() : "";
        if (line.isEmpty() || line.startsWith("SN=") || line.contains("=")) {
            return;
        }

        // ATTLOG formats:
        //   EmpID<TAB>yyyy-MM-dd HH:mm:ss<TAB>status<TAB>verify...
        //   EmpID Date Time status verify...
        String[] tokens = line.split("\\s+");
        if (tokens.length < 3) {
            log.warn("Skipping unparseable ADMS punch line from SN={}: {}", serialNumber, line);
            return;
        }
        log.info("incoming punch data: {}", tokens);

        String employeeId = tokens[0];
        String punchDate = tokens[1];
        String punchTime = tokens[2];

        log.info("ADMS punch SN={} empId={} date={} time={}", serialNumber, employeeId, punchDate, punchTime);
        deviceAttendanceLogService.savePunchLog(serialNumber, table, employeeId, punchDate, punchTime, line);
    }

}
