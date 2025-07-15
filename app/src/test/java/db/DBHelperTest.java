package vsas.db;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class DBHelperTest {
	private static DBHelper dbHelper;

	@BeforeAll
	public static void setUp() {
		dbHelper = DBHelper.getInstance(false);
		dbHelper.resetSchema();
	}

	@BeforeEach
	public void initDB() {
		dbHelper.resetDatabase();
	}	

	@Test
	public void testCheckDefaultUsersPresent() {

		ArrayList<String> users = dbHelper.getAllUsers();
		assertTrue(users.contains("normal_user"),
				"Normal user not in database");
		assertTrue(users.contains("admin_user"),
				"Admin user not in database");
	}

	@Test
	public void testPasswordHash() {
		String defUser1 = "normal_user";
		String pass1 = "password";

		String defUser2 = "admin_user";
		String pass2 = "12345";
		
		assertEquals(dbHelper.getPasswordFromDB(defUser1),
				dbHelper.getHashString(pass1),
				"Failed password hash test for default user");
		assertEquals(dbHelper.getPasswordFromDB(defUser2),
                                dbHelper.getHashString(pass2),
                                "Failed password hash test for admin user");
	}

	@Test
	public void testAddAndGetUser() {
		String userid = "new_user";
		String password = "newpassword";
		String utype = "normal";
		String phonenum = "0123456789";
		String email = "test@test.com";
		String fullname = "New User";

		dbHelper.addUser(userid, password, utype, phonenum, email,
				fullname);

		assertTrue(dbHelper.getAllUsers().contains("new_user"),
                                "New user not in database");

		assertNull(dbHelper.getUserFromDB("doesn't exist"));
		HashMap<String,String> newUser = dbHelper.getUserFromDB(userid);

		assertEquals(newUser.get("userid"), userid);
		assertEquals(newUser.get("password"), 
				dbHelper.getHashString(password));
		assertEquals(newUser.get("utype"), utype);
		assertEquals(newUser.get("phonenum"), phonenum);
		assertEquals(newUser.get("email"), email);
		assertEquals(newUser.get("fullname"), fullname);

	}

	@Test
	public void testUserType() {
		assertEquals("normal", dbHelper.getUserType("normal_user"));
		assertEquals("admin", dbHelper.getUserType("admin_user"));
		assertNull(dbHelper.getUserType("doesn't exist"));
	}


	@Test
	public void testCheckUser() {
		assertTrue(dbHelper.checkUserIdExists("normal_user"));
		assertTrue(dbHelper.checkUserIdExists("admin_user"));
		assertFalse(dbHelper.checkUserIdExists("does not"));
	}

	@Test
	public void testUserUpdate() {
		String userid = "normal_user";
		String password = "new pass";
		String utype = "admin";
		String phonenum = "1234567";
		String email = "abc@123.com";
		String fullname = "John Smith";

		dbHelper.updateUser(userid, password, utype, phonenum, email,
			       fullname);

		HashMap<String,String> user = dbHelper.getUserFromDB(userid);

		assertEquals(dbHelper.getHashString(password),
				user.get("password"));
		assertEquals(utype, user.get("utype"));
                assertEquals(phonenum, user.get("phonenum"));
                assertEquals(email, user.get("email"));
                assertEquals(fullname, user.get("fullname"));
	}

	@Test
	public void testUserIdUpdate() {
		String userid = "normal_user";
		HashMap<String,String> old = dbHelper.getUserFromDB(userid);

		String new_uid = "obliviat";
		dbHelper.updateUserId(userid, new_uid);

		old.remove("userid");

		assertTrue(dbHelper.checkUserIdExists(new_uid));
		assertFalse(dbHelper.checkUserIdExists(userid));

		HashMap<String,String> updated = dbHelper.getUserFromDB(new_uid);
		updated.remove("userid");

		assertEquals(old, updated);
	}

	@Test
	public void testUserDelete() {
		String userid = "normal_user";

		dbHelper.deleteUser(userid);
		assertFalse(dbHelper.checkUserIdExists(userid));
		assertNull(dbHelper.getUserFromDB(userid));
	}

	@Test
	public void testGoodPhonenum() {
		assertTrue(dbHelper.goodPhonenum(""));
		assertTrue(dbHelper.goodPhonenum("0123456789"));
		assertFalse(dbHelper.goodPhonenum("1234567890998"));
	}
}
