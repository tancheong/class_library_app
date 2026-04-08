package com.tenco.library.view;

import com.tenco.library.dto.Book;
import com.tenco.library.dto.Student;
import com.tenco.library.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자 입출력을 처리하는 View 클래스
public class LibraryView2 {
    private final LibraryService service = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 프로그램 메인 루프
    public void start() {
        System.out.println("도서 관리 시스템 시작...");

        while (true) {
            printMenu();
            int choice = readInt("선택: ");

            try {
                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        listBooks();
                        break;
                    case 3:
                        searchBooks();
                        break;
                    case 4:
                        addStudent();
                        break;
                    case 5:
                        listStudents();
                        break;
                    case 6:
                        borrowBooks();
                        break;
                    case 7:
                        listBorrowedBooks();
                        break;
                    case 8:
                        returnBook();
                        break;
                    case 9:
                        login();
                        break;
                    case 10:
                        logout();
                        break;
                    case 11:
                        System.out.println("프로그램을 종료합니다.");
                        scanner.close();
                        return; // while문 종료 처리
                    default:
                        System.out.println("1 ~ 11 사이의 숫자를 입력하세요.");
                }
            } catch (Exception e) {
                System.out.println("오류: " + e.getMessage());
            }
        }
    }

    // 10. 로그아웃
    private void logout() {
        if (currentStudentId == null) {
            System.out.println("현재 로그인 상태가 아닙니다.");
        } else {
            System.out.println(currentStudentName + "님이 로그아웃 되었습니다.");
            currentStudentId = null;
            currentStudentName = null;
        }
    }

    // 9. 로그인 <-- 스캐너에서 9번 눌러졌음
    private void login() throws SQLException {
        if (currentStudentId != null) {
            System.out.println("이미 로그인중입니다. (" + currentStudentName + ")");
            return;
        }
        System.out.print("학번: ");
        String studentId = scanner.nextLine().trim(); // 학번 PK 아님

        // 유효성 검사
        if (studentId.isEmpty()) {
            System.out.println("학번을 입력해주세요.");
            return;
        }
        Student student = service.authenticateStudent(studentId);
        if (student == null) {
            System.out.println("존재하지 않는 학번입니다.");
        } else {
            currentStudentId = student.getId();
            currentStudentName = student.getName();
            System.out.println(currentStudentName + "님, 환영합니다.");
        }

    }

    // 8. 도서 반납
    private void returnBook() throws SQLException {
    }

    // 7. 대출 중인 도서
    private void listBorrowedBooks() throws SQLException {
        List<Book> bookList = service.getAllBooks();
        System.out.println("현재 대출 중인 도서");
        System.out.println();

        for (Book b : bookList) {
            if (b.isAvailable() == false) {
                System.out.println(b.getTitle());

            }
        }
    }

    // 6. 도서 대출
    private void borrowBooks() throws SQLException {
        System.out.print("대출할 도서 제목: ");
        String bookTitle = scanner.nextLine().trim();
        System.out.print("빌리는 학생 ID: ");
        String studentId = scanner.nextLine().trim();

        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            System.out.println("입력해주세요.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            System.out.println("입력해주세요.");
        }

        List<Book> bookList = service.searchBooksByTitle(bookTitle);
        Student student = service.authenticateStudent(studentId);
        service.borrowBook(bookList.get(0).getId(), student.getId());



//        System.out.println("빌릴 도서 제목: ");
//        int bookId = readInt("도서 ID 입력: ");
//        int studentId = readInt("학생 ID 입력: ");

//        service.borrowBook(bookId, studentId);




    }

    // 5. 학생 목록
    private void listStudents() throws SQLException {
        List<Student> studentList = service.getAllStudent();
        if (studentList.isEmpty()) {
            System.out.println("등록된 학생이 없습니다.");
        } else {
            System.out.println("------------------------");
            for (Student s : studentList) {
                System.out.println("학생 이름: " + s.getName() + " | 학생 학번: " + s.getStudentId());
            }
        }
    }

    // 4. 학생 등록
    private void addStudent() throws SQLException {
        System.out.print("학생 이름: ");
        String studentName = scanner.nextLine().trim();
        if (studentName.isEmpty()) {
            System.out.println("등록할 학생의 이름을 입력해주세요.");
            return;
        }

        System.out.print("학생 학번: ");
        String studentId = scanner.nextLine().trim();
        if (studentId.isEmpty()) {
            System.out.println("등록할 학생의 이름을 입력해주세요.");
            return;
        }
        Student student = Student.builder()
                .name(studentName)
                .studentId(studentId)
                .build();

        service.addStudent(student);
        System.out.println("학생 이름: " + studentName);
        System.out.println("학생 학번: " + studentId);
    }

    // 3. 도서 검색
    private void searchBooks() throws SQLException {
        System.out.print("검색 제목: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("검색어를 입력하세요");
            return;
        }
        List<Book> bookList = service.searchBooksByTitle(title);
        if (bookList.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            for (Book b : bookList) {
                System.out.printf("ID: %2d | %-30s | %-15s | %s%n",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }
    }

    // 2. 도서 목록
    private void listBooks() throws SQLException {
        List<Book> bookList = service.getAllBooks();
        if (bookList.isEmpty()) {
            System.out.println("등록된 도서가 없습니다.");
        } else {
            System.out.println("------------------------");
            for (Book b : bookList) {
                System.out.printf("ID: %2d | %-30s | %-15s | %s%n",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }
    }

    // 1. 도서 추가
    private void addBook() throws SQLException {
        System.out.print("제목: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("제목은 필수입니다.");
            return;
        }

        // 제목
        System.out.print("저자: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("저자는 필수입니다.");
            return;
        }

        // 출판사
        System.out.print("출판사: ");
        String publisher = scanner.nextLine().trim();

        // 출판년도
        int publisherYear = readInt("출판년도: ");

        // ISBN
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();

        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher.isEmpty() ? null : publisher)
                .publicationYear(publisherYear)
                .isbn(isbn.isEmpty() ? null : isbn)
                .available(true)
                .build();

        service.addBook(book);
        System.out.println("도서 추가: " + title);
    }

    private Integer currentStudentId = null; // 로그인 중인 학생의 DB ID 저장
    private String currentStudentName = null; // 로그인 중인 학생 이름

    private void printMenu() {
        System.out.println("\n==== 도서 관리 시스템 ====");

        System.out.println("---------------------------------------------------");
        System.out.println("1. 도서 추가");
        System.out.println("2. 도서 목록");
        System.out.println("3. 도서 검색");
        System.out.println("4. 학생 등록");
        System.out.println("5. 학생 목록");
        System.out.println("6. 도서 대출");
        System.out.println("7. 대출 중인 도서");
        System.out.println("8. 도서 반납");
        System.out.println("9. 로그인");
        System.out.println("10. 로그아웃");
        System.out.println("11. 종료");
    }

    // 숫자 입력을 안전하게 처리 (잘못된 입력시 재요청)
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요");
            }
        }
    }
} // end of class
