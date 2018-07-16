package helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BookDAO {

	private String jdbcURL;
	private String jdbcUsername;
	private String jdbcPassword;
	private Connection jdbcConnection;

	public BookDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
		super();
		this.jdbcURL = jdbcURL;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
	}

	protected void connect() throws SQLException {
		if (jdbcConnection == null || jdbcConnection.isClosed()) {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		}
	}

	protected void disconnect() throws SQLException {
		if (jdbcConnection != null && !jdbcConnection.isClosed()) {
			jdbcConnection.close();
		}
	}

	public boolean insertBook(Book book) throws SQLException {
		boolean success = false;

		String sql = "INSERT INTO BOOK (title, author, price) VALUES (?,?,?)";
		connect();
		PreparedStatement ps = jdbcConnection.prepareStatement(sql);
		ps.setString(1, book.getTitle());
		ps.setString(2, book.getAuthor());
		ps.setFloat(3, book.getPrice());

		success = ps.executeUpdate() > 0;

		disconnect();

		return success;
	}

	public ArrayList<Book> listAllBooks() throws SQLException {
		ArrayList<Book> listBook = new ArrayList<>();

		String sql = "SELECT * FROM BOOK";

		connect();

		Statement st = jdbcConnection.createStatement();

		ResultSet rs = st.executeQuery(sql);

		while (rs.next()) {
			int id = rs.getInt("book_id");
			String title = rs.getString("title");
			String author = rs.getString("author");
			float price = rs.getFloat("price");

			listBook.add(new Book(id, title, author, price));
		}

		rs.close();
		st.close();

		disconnect();

		return listBook;
	}

	public boolean deleteBook(Book book) throws SQLException {
		boolean success = false;

		String sql = "DELETE FROM BOOK WHERE book_id = ?";

		connect();

		PreparedStatement ps = jdbcConnection.prepareStatement(sql);
		ps.setInt(1, book.getId());

		success = ps.executeUpdate() > 0;

		ps.close();

		disconnect();

		return success;
	}

	public boolean updateBook(Book book) throws SQLException {
		boolean success = false;

		String sql = "UPDATE BOOK SET title=?,author=?,price=? WHERE book_id=?";
		connect();

		PreparedStatement ps = jdbcConnection.prepareStatement(sql);

		ps.setString(1, book.getTitle());
		ps.setString(2, book.getAuthor());
		ps.setFloat(3, book.getPrice());
		ps.setInt(4, book.getId());

		success = ps.executeUpdate() > 0;

		ps.close();
		disconnect();

		return success;
	}

	public Book getBook(int id) throws SQLException {
		Book book = null;
		String sql = "SELECT * FROM book WHERE book_id = ?";

		connect();

		PreparedStatement statement = jdbcConnection.prepareStatement(sql);
		statement.setInt(1, id);

		ResultSet resultSet = statement.executeQuery();

		if (resultSet.next()) {
			String title = resultSet.getString("title");
			String author = resultSet.getString("author");
			float price = resultSet.getFloat("price");

			book = new Book(id, title, author, price);
		}

		resultSet.close();
		statement.close();

		return book;
	}

}
