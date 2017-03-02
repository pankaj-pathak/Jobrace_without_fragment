package grid.jobrace.admin.jobrace.data_classes;

/**
 * Created by Pankaj on 11/16/2016.
 */

public class Job_details_data
{
    private String id;
    private String CompanyName;
    private String JobTitle;
    private String JobDescription;
    private String SalaryOfferedMin;
    private String SalaryOfferedMax;
    private String ExperienceRequiredMin;
    private String ExperienceRequiredMax;
    private String InterviewLocation;
    private String DateTimeOfInterview;
    private String ApplyBefore;
    private String JobEligibility;
    private String IsActive;
    private String CreatedDate;
    private String EmployerEmail;
    private String Technology;
    private String Logo;
    private String Skills;

    public Job_details_data()
    {

    }

    public String getJobid() {
        return id;
    }

    public void setJobid(String jobid) {
        this.id = jobid;
    }

    public String getJob_title() {
        return JobTitle;
    }

    public void setJob_title(String job_title) {
        this.JobTitle = job_title;
    }

    public String getJob_description() {
        return JobDescription;
    }

    public void setJob_description(String job_description) {
        this.JobDescription = job_description;
    }

    public String getSalary_offer_min() {
        return SalaryOfferedMin;
    }

    public void setSalary_offer_min(String salary_offer_min) {
        this.SalaryOfferedMin = salary_offer_min;
    }

    public String getSalary_offer_max() {
        return SalaryOfferedMax;
    }

    public void setSalary_offer_max(String salary_offer_max) {
        this.SalaryOfferedMax = salary_offer_max;
    }

    public String getExp_req_min() {
        return ExperienceRequiredMin;
    }

    public void setExp_req_min(String exp_req_min) {
        this.ExperienceRequiredMin = exp_req_min;
    }

    public String getExp_req_max() {
        return ExperienceRequiredMax;
    }

    public void setExp_req_max(String exp_req_max) {
        this.ExperienceRequiredMax = exp_req_max;
    }

    public String getInterview_location() {
        return InterviewLocation;
    }

    public void setInterview_location(String interview_location) {
        this.InterviewLocation = interview_location;
    }

    public String getDate_time_of_interview() {
        return DateTimeOfInterview;
    }

    public void setDate_time_of_interview(String date_time_of_interview) {
        this.DateTimeOfInterview = date_time_of_interview;
    }

    public String getApply_before() {
        return ApplyBefore;
    }

    public void setApply_before(String apply_before) {
        this.ApplyBefore = apply_before;
    }

    public String getJob_eligibility() {
        return JobEligibility;
    }

    public void setJob_eligibility(String job_eligibility) {
        this.JobEligibility = job_eligibility;
    }

    public String getIs_active() {
        return IsActive;
    }

    public void setIs_active(String is_active) {
        this.IsActive = is_active;
    }

    public String getCreated_date() {
        return CreatedDate;
    }

    public void setCreated_date(String created_date) {
        this.CreatedDate = created_date;
    }

    public String getEmployer_email() {
        return EmployerEmail;
    }

    public void setEmployer_email(String employer_email) {
        this.EmployerEmail = employer_email;
    }

    public String getTechnology() {
        return Technology;
    }

    public void setTechnology(String technology) {
        this.Technology = technology;
    }

    public String getCompany_name() {
        return CompanyName;
    }

    public void setCompany_name(String company_name) {
        this.CompanyName = company_name;
    }

    public String getLogo_url() {
        return Logo;
    }

    public void setLogo_url(String logo_url) {
        this.Logo = logo_url;
    }

    public String getSkills() {
        return Skills;
    }

    public void setSkills(String skills) {
        Skills = skills;
    }
}
