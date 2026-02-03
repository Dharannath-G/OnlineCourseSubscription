package com.course.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.course.bean.Enrollment;
import com.course.util.DBUtil;

public class EnrollmentDAO {

  
    public String generateEnrollmentID() {
        String enrollmentID = null;
        String sql = "SELECT 'ENR' || LPAD(NVL(MAX(SUBSTR(ENROLLMENT_ID,4)),0)+1,4,'0') FROM ENROLLMENT_TBL";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                enrollmentID = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollmentID;
    }

   
    public boolean recordEnrollment(Enrollment enroll) {
        boolean status = false;

        String sql = "INSERT INTO ENROLLMENT_TBL " +
                     "(ENROLLMENT_ID, COURSE_ID, STUDENT_ID, STUDENT_NAME, AMOUNT_PAID, ENROLLMENT_DATE, STATUS) " +
                     "VALUES (?,?,?,?,?,SYSDATE,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, enroll.getEnrollmentID());
            ps.setString(2, enroll.getCourseID());
            ps.setString(3, enroll.getStudentID());
            ps.setString(4, enroll.getStudentName());
            ps.setDouble(5, enroll.getAmountPaid());
            ps.setString(6, enroll.getStatus());

          

            status = ps.executeUpdate() > 0;
            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

   
    public boolean cancelEnrollment(String enrollmentID) {
        boolean status = false;

        String sql = "UPDATE ENROLLMENT_TBL SET STATUS='CANCELLED' WHERE ENROLLMENT_ID=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, enrollmentID);
            status = ps.executeUpdate() > 0;
            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

   
    public int getActiveEnrollmentCount(String courseID) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM ENROLLMENT_TBL WHERE COURSE_ID=? AND STATUS='ACTIVE'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ps.setString(1, courseID);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    
    public List<Enrollment> getEnrollmentHistory(double studentID) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENT_TBL WHERE STUDENT_ID=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, studentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentID(rs.getString("ENROLLMENT_ID"));
                e.setCourseID(rs.getString("COURSE_ID"));
                e.setStudentID(rs.getString("STUDENT_ID"));
                e.setStudentName(rs.getString("STUDENT_NAME"));
                e.setAmountPaid(rs.getInt("AMOUNT_PAID"));
                e.setEnrollmentDate(rs.getDate("ENROLLMENT_DATE"));
                e.setStatus(rs.getString("STATUS"));

                list.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
