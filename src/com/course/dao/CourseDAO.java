package com.course.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.course.bean.Course;
import com.course.util.DBUtil;

public class CourseDAO {

    
    public Course findCourse(String courseID) {
        Course course = null;
        String sql = "SELECT * FROM COURSE_TBL WHERE COURSE_ID = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseID(rs.getString("COURSE_ID"));
                course.setTitle(rs.getString("Title"));
                course.setPrice(rs.getDouble("PRICE"));
                course.setAvailableSeats(rs.getInt("AVAILABLE_SEATS"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return course;   
    }

   
    public List<Course> viewAllCourses() {
        List<Course> courseList = new ArrayList<>();
        String sql = "SELECT * FROM COURSE_TBL";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseID(rs.getString("COURSE_ID"));
                course.setTitle(rs.getString("Title"));
                course.setPrice(rs.getDouble("PRICE"));
                course.setAvailableSeats(rs.getInt("AVAILABLE_SEATS"));

                courseList.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseList;
    }

    
    public boolean insertCourse(Course course) {
        boolean status = false;
        String sql = "INSERT INTO COURSE_TBL VALUES (?,?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, course.getCourseID());
            ps.setString(2, course.getTitle());
            ps.setDouble(3, course.getPrice());
            ps.setInt(4, course.getAvailableSeats());

            status = ps.executeUpdate() > 0;
            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    
    public boolean updateAvailableSeats(String courseID, int newCount) {
        boolean status = false;
        String sql = "UPDATE COURSE_TBL SET AVAILABLE_SEATS = ? WHERE COURSE_ID = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newCount);
            ps.setString(2, courseID);

            status = ps.executeUpdate() > 0;
            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    
    public boolean deleteCourse(String courseID) {
        boolean status = false;
        String sql = "DELETE FROM COURSE_TBL WHERE COURSE_ID = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseID);
            status = ps.executeUpdate() > 0;
            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
