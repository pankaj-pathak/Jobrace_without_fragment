package com.example.pankaj.jobrace_without_fragment.interfaces;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by Pankaj on 11/22/2016.
 */

public interface Custom_interface_test
{
    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("LoginNew")
    Call<String> login(@Field("Email") String email, @Field("Password") String password, @Field("IMEINumber") String imei);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("QuestionNew")
    Call<String> getQuestions(@Field("Id") String questionid);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("JobpostingNew")
    Call<String> get_jobs(@Field("Technology") String value, @Field("Cardtype") String type, @Field("Id") int position);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("TodaysInterview")
    Call<String> get_todays_jobs(@Field("Technology") String value, @Field("Cardtype") String type, @Field("Id") int position);


    @Headers("Token:adminjobrace54321")
    @GET("GeturlNew")
    Call<String> getUrl();

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("useractivation")
    Call<String> verify_user(@Field("Email") String email, @Field("VerificationCode") String verification_code);

    @Headers("Token:adminjobrace54321")
    @GET("CandidatetechnologyNew")
    Call<String> getTechnology();


    @Headers("Token:adminjobrace54321")
    @GET("CandidateRegistrationTechnologyNew")
    Call<String> getRegistrationTechnology();

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("OnebyonequestionsNew")
    Call<String> getQuestionsId(@Field("Technology") String technology);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("VirtualInterviewQuestionsList")
    Call<String> getQuestionsIdForVirtualInterview(@Field("Skills") String skills,@Field("Experience") String experience);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("TeststartNew")
    Call<String> startTest(@Field("Email") String email, @Field("Jobid") String jobid, @Field("Techid") String Techid);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("VirtualInterviewStart")
    Call<String> startVirtualInterview(@Field("Email") String email, @Field("Jobid") String jobid, @Field("Techid") String Techid);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("AnswerNew")
    Call<String> sendAnswer(@Field("Email") String email, @Field("QId") String qid, @Field("ans") String answer, @Field("Qno") String qno);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("AnswerVirtualInterview")
    Call<String> sendVirtualInterviewAnswer(@Field("Email") String email, @Field("QId") String qid, @Field("ans") String answer, @Field("Qno") String qno);


    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("UserregNew")
    Call<String> registerUser(@Field("FullName") String fullname, @Field("Email") String email, @Field("Password") String password, @Field("Technology") String technology, @Field("ContactNo") String contactno, @Field("IMEINumber") String imei,@Field("Experience") String experience);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("PasswordrstNew")
    Call<String> sendOtp(@Field("Email") String email, @Field("otp") String password);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("ChangepasswordNew")
    Call<String> changePassword(@Field("Email") String email, @Field("Password") String password);

    @Headers("Token:adminjobrace54321")
    @GET("GetMenuListNew")
    Call<String> getMenuList();

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("GetcandidateinfoNew")
    Call<String> getCandidateInfo(@Field("Email") String email, @Field("Password") String password);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("GetSkillTestResultNew")
    Call<String> getSkillTestResult(@Field("Email") String email);


    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("GetVirtualInterviewResult")
    Call<String> getVirtualInterviewResult(@Field("Email") String email, @Field("Jobid") String jobid, @Field("FromQNo") int fromQNo, @Field("ToQNo") int toQNo);

    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("Test_virtual_interview_result_try")
    Call<String> sendMailToEmployer(@Field("Name") String name,@Field("Email") String email,@Field("Phone") String phone,@Field("Technology") String technology,@Field("EmployerEmail") String employer_email, @Field("Jobid") String jobid, @Field("FromQNo") int fromQNo, @Field("ToQNo") int toQNo);



    @FormUrlEncoded
    @Headers("Token:adminjobrace54321")
    @POST("GetProgressReportOfCandidateNew")
    Call<String> getProgressReportOfCandidate(@Field("Email") String email);

}
