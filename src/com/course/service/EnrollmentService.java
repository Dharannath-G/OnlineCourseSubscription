package com.course.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.course.bean.Course;
import com.course.bean.Enrollment;
import com.course.dao.CourseDAO;
import com.course.dao.EnrollmentDAO;
import com.course.util.ActiveEnrollmentException;
import com.course.util.CourseFullException;
import com.course.util.DBUtil;
import com.course.util.ValidationException;

public class EnrollmentService {

    private CourseDAO courseDAO = new CourseDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    
    public Course viewCourseDetails(String courseID) {
        if (courseID == null || courseID.length()==0) {
            return null;
        }
        return courseDAO.findCourse(courseID);
    }

    
    public List<Course> viewAllCourses() {
        return courseDAO.viewAllCourses();
    }

   
    public boolean addNewCourse(Course course) throws ValidationException {

        if (course == null ||
            course.getCourseID() == null || course.getCourseID().isEmpty() ||
            course.getTitle() == null || course.getTitle().isEmpty() ||
            course.getPrice() < 0 ||
            course.getAvailableSeats() <= 0) {

            throw new ValidationException("Invalid course details");
        }

        if (courseDAO.findCourse(course.getCourseID()) != null) {
            throw new ValidationException("Course ID already exists");
        }

        return courseDAO.insertCourse(course);
    }

   
    public boolean removeCourse(String courseID)
            throws ValidationException, ActiveEnrollmentException {

        if (courseID == null || courseID.trim().isEmpty()) {
            throw new ValidationException("Course ID cannot be empty");
        }

        int activeCount = enrollmentDAO.getActiveEnrollmentCount(courseID);
        if (activeCount > 0) {
            throw new ActiveEnrollmentException("Active enrollments exist");
        }

        return courseDAO.deleteCourse(courseID);
    }

   
    public boolean enrollStudent(String courseID,
                                 String studentID,
                                 String studentName,
                                 double paymentAmount,
                                 Date enrollmentDate)
            throws ValidationException, CourseFullException {

       
        if (courseID == null || courseID.isEmpty() ||
            studentID == null || studentID.isEmpty() ||
            studentName == null || studentName.isEmpty() ||
            paymentAmount < 0 ||
            enrollmentDate == null) {

            throw new ValidationException("Invalid enrollment inputs");
        }

        Course course = courseDAO.findCourse(courseID);
        if (course == null) {
            return false;
        }

        
        if (course.getAvailableSeats() <= 0) {
            throw new CourseFullException("No seats available");
        }

        if (paymentAmount < course.getPrice()) {
            throw new ValidationException("Insufficient payment");
        }

        Connection con = null;

        try {
            
            con = DBUtil.getConnection();

            int newAvailable = course.getAvailableSeats() - 1;
            courseDAO.updateAvailableSeats(courseID, newAvailable);

           
            boolean paymentSuccess = true;
            if (!paymentSuccess) {
                con.rollback();
                return false;
            }

            
            Enrollment enroll = new Enrollment();
            enroll.setEnrollmentID(enrollmentDAO.generateEnrollmentID());
            enroll.setCourseID(courseID);
            enroll.setStudentID(studentID);
            enroll.setStudentName(studentName); 
            enroll.setAmountPaid((int) paymentAmount);
            enroll.setStatus("ENROLLED");

            enrollmentDAO.recordEnrollment(enroll);

           
            con.commit();
            return true;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    
    public boolean cancelEnrollment(String enrollmentID, boolean issueRefund)
            throws ValidationException {

        if (enrollmentID == null || enrollmentID.trim().isEmpty()) {
            throw new ValidationException("Invalid enrollment ID");
        }

        Connection con = null;

        try {
            con = DBUtil.getConnection();

            

            enrollmentDAO.cancelEnrollment(enrollmentID);

            
            if (issueRefund) {
             
            }

            con.commit();
            return true;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
