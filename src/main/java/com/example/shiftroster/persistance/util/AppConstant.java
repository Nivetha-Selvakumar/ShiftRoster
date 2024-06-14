package com.example.shiftroster.persistance.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppConstant {

    public static final String EMP_ID = "EmpId";

    public static final String EXCEL_DATE_FORMAT = "dd/MM/yyyy";

    public static final String EXCEL_DAY_FORMAT = "EEE";

    public static final String TEMPLATE_NOT_FOUND = "Template Not Found.Enter valid template";

    public static final String EMPLOYEE_NOT_FOUND = "Employee not found.";

    public static final String EXCEL_APPLICATION = "application/vnd.ms-excel";

    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String INVALID_DATE_RANGE = "Selected End date is before start date. Enter valid date range.";

    public static final String SUCCESSFULLY_UPLOAD = "Successfully uploaded!";

    public static final String SHIFT_NOT_FOUND = "Shift not found";

    public static final Integer MIN_SHIFT_DIFFERENCE = 8;

    public static final String INVALID_FILE = "Uploaded file is not in Excel format.";

    public static final String UN_ASSIGNED_SHIFT = "UA";

    public static final String DATE_REGEX = "^(?:19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])$";

    public static final String INVALID_DATE = "Invalid date. Enter correct date and should in format yyyymmdd";

    public static final String EMP_ID_NULL_ERROR = "Emp Id is not valid. Template type cannot be null or empty";

    public static final String EMP_ID_LENGTH_NOT_VALID = "Emp Id size should not exceed 20 ";

    public static final String EMP_ID_REGEX = "^[0-9]*$";


    public static final String EMP_ID_FORMAT_INVALID = "Emp Id should be only numbers";

    public static final int EMP_ID_LENGTH = 20;


    public static final int DATE_STRING_LENGTH = 8;

    public static final String DATE_NULL_ERROR = "Date is not valid. Template type cannot be null or empty";

    public static final String DATE_LENGTH_INVALID = "Date size should not exceed 8";


    public static final String TEMPLATE_TYPE_INVALID = "Template Type is not valid. Template type cannot be null or empty";

    public static final String TEMPLATE_TYPE_REGEX = "^[A-Za-z]$";

    public static final String TEMPLATE_TYPE_FORMAT_INVALID = "Template Type should not have any numbers or special character";

    public static final int TEMPLATE_LENGTH = 25;

    public static final String TEMPLATE_LENGTH_INVALID = "Template type size should not exceed 25 ";

    public static final String FILE_NOT_EMPTY = "File cannot be null or empty";

    public static final String HEADER_ROW_MISSING = "Header row is missing.";

    public static final String HEADER_INVALID = "Headers invalid. Check the headers";

    public static final String INVALID_EMP_ID = "Invalid EmpId at row ";

    public static final String INVALID_MISSING_EMAIL = "Invalid or missing email for appraiser: ";

    public static final String FAILED_TO_SEND_MAIL = "Failed to send email to: ";

    public static final String ERROR_IN_SENDING_REMINDER = "An error occurred in sendReminderTask: ";

    public static final String DAY = "day";

    public static final String STRING_2D_FORMAT = "%02d";

    public static final String INVALID_DAY = "Invalid day: ";

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static final String EMAIL_SUBJECT = "Unassigned Shifts Notification";

    public static final String HTML_DATE_LIST = "<p>Employee Name: %s</p><p>Employee Code: %s</p><p>Formatted Dates: %s</p>";

    public static final String SUN = "SUN";

    public static final String MON = "MON";

    public static final String TUE = "TUE";

    public static final String WED = "WED";

    public static final String THU = "THU";

    public static final String FRI = "FRI";

    public static final String SAT = "SAT";

    public static final String EMPTY_STRING = "";

    public static final String EXCEL_ONLY_ALLOWED = "Invalid file type. Only Excel files are allowed.";

    public static final String CELL_VALUE_REGEX = "[a-zA-Z0-9 ]*";

    public static final String REMINDER_SENT_SUCCESSFULLY = "Reminder set successfully";

    public static final Object TEMPLATE_DOWNLOADED = "Template Downloaded";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String FILE_NAME = "attachment; filename=shift_roster_template.xlsx";

    public static final String INVALID_ROW = "Invalid data in Row number : ";

    public static final String DAY_NOT_MATCH_DATE = "Day does not match the date in header: ";

    public static final String STRING_SPACE = " ";

    public static final String DAY_LOWERCASE_INVALID = " Or day format invalid. Day should be in uppercase EEE format: ";

    public static final String NOT_EMP_ID = "Header Column 1 must only be EmpId";

    public static final String OPEN_BRACKET = "(";

    public static final String CLOSE_BRACKET = ")";

    public static final String NO_SHIFT_ASSIGNED_SUBJECT = "No Shift Assigned";

    public static final String HTML_REPORTER_INFO = "<li>%s - %s: %s</li>";

    public static final String HTML_NO_SHIFT_ASSIGNED = "<p>No shifts assigned for the employee %s to this month .</p>";
}
