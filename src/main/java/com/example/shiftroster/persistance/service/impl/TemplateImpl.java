package com.example.shiftroster.persistance.service.impl;

import com.example.shiftroster.persistance.Enum.EnumDocType;
import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.Exception.MisMatchException;
import com.example.shiftroster.persistance.Exception.NotFoundException;
import com.example.shiftroster.persistance.secondary.entity.TemplateEntity;
import com.example.shiftroster.persistance.secondary.repository.TemplateRepo;
import com.example.shiftroster.persistance.service.TemplateService;
import com.example.shiftroster.persistance.util.AppConstant;
import com.example.shiftroster.persistance.util.DateTimeUtil;
import com.example.shiftroster.persistance.validation.BusinessValidation;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class TemplateImpl implements TemplateService {

    @Autowired
    TemplateRepo templateRepo;

    @Autowired
    BusinessValidation businessValidation;

    @Override
    public void generateShiftRosterTemplate(String templateType, String startDate, String endDate, String empId, HttpServletResponse response) throws CommonException, IOException, ParseException {
        TemplateEntity templateEntity = templateRepo.findByDocTypeAndRefType(EnumDocType.EXCEL, templateType)
                .orElseThrow(() -> new NotFoundException(AppConstant.TEMPLATE_NOT_FOUND));

        businessValidation.employeeValidation(empId);

        String filePath = templateEntity.getDocumentFile();
        SimpleDateFormat outputFormat = new SimpleDateFormat(AppConstant.EXCEL_DATE_FORMAT);
        SimpleDateFormat dayFormat = new SimpleDateFormat(AppConstant.EXCEL_DAY_FORMAT);

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setBold(true);
            style.setFont(font);

            Row headerRow = sheet.createRow(0);
            int cellIndex = 0;

            Cell empIdCell = headerRow.createCell(cellIndex++);
            empIdCell.setCellValue(AppConstant.EMP_ID);
            empIdCell.setCellStyle(style);

            Date start = DateTimeUtil.convertStringDateFormat(startDate);
            Date end = DateTimeUtil.convertStringDateFormat(endDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
                if (start.after(end)) {
                    throw new MisMatchException(AppConstant.INVALID_DATE_RANGE);
                } else {
                    while (!calendar.getTime().after(end)) {
                        String formattedDate = outputFormat.format(calendar.getTime());
                        String dayOfWeek = dayFormat.format(calendar.getTime());
                        String header = formattedDate + " (" + dayOfWeek.toUpperCase() + ")";

                        Cell cell = headerRow.createCell(cellIndex++);
                        cell.setCellValue(header);
                        cell.setCellStyle(style);

                        calendar.add(Calendar.DATE, 1);
                    }

                    for (int i = 0; i < cellIndex; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    //The type of content being returned to the server
                    response.setContentType(AppConstant.EXCEL_CONTENT_TYPE);
                    //It shows the file downloading in the given name
                    response.setHeader(AppConstant.CONTENT_DISPOSITION, AppConstant.FILE_NAME);
                    try (OutputStream outputStream = response.getOutputStream()) {
                        workbook.write(outputStream);
                    }
                }
        }
    }
}
