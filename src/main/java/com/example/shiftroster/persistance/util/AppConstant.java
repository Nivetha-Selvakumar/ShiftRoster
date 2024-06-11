package com.example.shiftroster.persistance.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppConstant {

    public static final String EMP_ID = "EmpId";

    public static final String EXCEL_DATE_FORMAT = "dd/MM/yyyy";

    public static final String EXCEL_DAY_FORMAT = "EEE";

    public static final String TEMPLATE_NOT_FOUND = "Template Not Found.Enter valid template";

    public static final String EMPLOYEE_NOT_FOUND = "Employee not found.";

    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String INVALID_DATE_RANGE = "Selected End date is before start date. Enter valid date range.";

    public static final String SUCCESSFULLY_UPLOAD = "Successfully uploaded!";

    public static final String SHIFT_NOT_FOUND = "Shift not found";

    public static final Integer MIN_SHIFT_DIFFERENCE = 8;

    public static final String INVALID_FILE = "Uploaded file is not in Excel format.";

    public static final String UN_ASSIGNED_SHIFT = "UA";
}
