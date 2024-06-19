package com.example.shiftroster.persistence.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppConstant {

    public static final String EMP_ID = "EmpId";

    public static final String EXCEL_DATE_FORMAT = "dd/MM/yyyy";

    public static final String EXCEL_DAY_FORMAT = "EEE";

    public static final String TEMPLATE_NOT_FOUND = "Template not found";

    public static final String INVALID_EMPLOYEE = "Invalid Employee. Login with valid user";

    public static final String EXCEL_APPLICATION = "application/vnd.ms-excel";

    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String INVALID_DATE_RANGE = "Invalid date range";

    public static final String SUCCESSFULLY_UPLOAD = "Successfully uploaded!";

    public static final String DATE_REGEX = "^(?:19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])$";

    public static final String INVALID_DATE = "Invalid date";

    public static final String INVALID_EMPLOYEE_ID = "Invalid employee Id ";

    public static final String EMP_ID_REGEX = "^[0-9]*$";

    public static final int EMP_ID_LENGTH = 20;

    public static final int DATE_STRING_LENGTH = 8;

    public static final String INVALID_TEMPLATE_TYPE = "Invalid Template type";

    public static final String TEMPLATE_TYPE_REGEX = "^[A-Za-z]$";

    public static final int TEMPLATE_LENGTH = 25;

    public static final String FILE_NOT_EMPTY = "No file uploaded";

    public static final String HEADER_INVALID = "Invalid headers";

    public static final String DAY = "day";

    public static final String STRING_2D_FORMAT = "%02d";

    public static final String INVALID_DAY = "Invalid day: ";

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static final String EMAIL_SUBJECT = "Unassigned Shifts Notification";

    public static final String SUN = "SUN";

    public static final String MON = "MON";

    public static final String TUE = "TUE";

    public static final String WED = "WED";

    public static final String THU = "THU";

    public static final String FRI = "FRI";

    public static final String SAT = "SAT";

    public static final String EMPTY_STRING = "";

    public static final String INVALID_FILE_TYPE = "Invalid file type.";

    public static final String CELL_VALUE_REGEX = "[a-zA-Z0-9 ]*";

    public static final String REMINDER_SENT_SUCCESSFULLY = "Reminder set successfully";

    public static final Object TEMPLATE_DOWNLOADED = "Template Downloaded";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String FILE_NAME = "attachment; filename=shift_roster_template.xlsx";

    public static final String INVALID_DATE_HEADER = "Invalid header. Date and day mismatch.";

    public static final String STRING_SPACE = " ";

    public static final String ALL_EMPLOYEES_HAVE_SHIFT = "All employees have shifts assigned.";

    public static final String ERROR = "ERROR";

    public static final String ERRORS = "ERRORS";

    public static final String NO_ACTIVE_EMPLOYEES_FOUND_FOR_APPRAISER = "No active employees found for some appraisal";

    public static final String ERROR_IN_SENDING_EMAIL = "Error in sending reminder emails.";

    public static final CharSequence COMMA_SPACE = ", ";

    public static final String HTML_LIST_DATA = "<li>%s [%s]: %s</li>";

    public static final String EMAIL_NOT_FOUND = "Email not valid";

    public static final String HTML_REPORTEES_UNASSIGNED_SHIFT = "<p>The following employees have unassigned shifts on the mentioned dates:</p><ul>%s</ul>";

    public static final String HTML_NO_SHIFT = "<p>The following employees have not been assigned any shifts for this month:</p><ul>%s</ul>";

    public static final String ROW = "Row %s :";

    public static final String UA = "UA";

    public static final String WO = "WO";

    public static final String INVALID_DATE_FORMAT = "Invalid date format: ";

    public static final String INVALID_SHIFT = "Invalid shift ";

    public static final String SET_DAY = "setDay";

    public static final String STRING_DAY_FORMAT = "%02d";

    public static final String INVALID_DATA_IN_ROW = "Invalid data in row no :";

    public static final String HTML_NO_SHIFT_CONTENT = "<li>%s [%s] </li>";

    public static final String ERROR_FILE_NAME = "attachment; filename=error_files.xlsx";

    public static final String GET_DAY = "getDay";

    public static final String MISSING_HEADER_VALUE = "Missing header values";

    public static final String WD = "WD";

    public static final String INVALID_WEEK_OFFS = "Employee %s has an invalid number of week-offs. The must be either 1 or 2 week offs in a week.";

    public static final String CONSECUTIVE_WORKING_DAYS = " Employee %s has more than 6 continuous working days starting from ";

    public static final String NO_SHIFT = "No shifts available for validation for employee ";

    public static final String INVALID_RESPONSE = "HttpServletResponse is not available.";

    public static final String INVALID_ATTRIBUTE = "No request attributes found. Ensure this method is called within an HTTP request context.";

    public static final String STRING_DATE = "yyyyMMdd";

    public static final String INVALID_EMPLOYEE_ROW = "Invalid employee id: %s in row no: %s ";

    public static final String NOT_REPORTEE = "Employee with employeeId %s is not your reportee.";

    public static final String EMP_ID_INVALID = "Emp id %s in row %s is invalid";

    public static final String EXPECTED_DAY = "(%s)";

    public static final String STRING_DATE_HEADER_FORMAT = " (%s)";

    public static final String EMPTY_CELL = "The cells should not be empty." ;
}
