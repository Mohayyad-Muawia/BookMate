package models;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String category;
    private String status;
    private Integer currentPage;
    private String notes;
    private String reminderDate;
    private int userId;

    public Book(String title, String author, String description, String category, String status,
            Integer currentPage, String notes, String reminderDate, int userId) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.status = status;
        this.currentPage = currentPage;
        this.notes = notes;
        this.reminderDate = reminderDate;
        this.userId = userId;
    }

    public Book() {
        // Default constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
