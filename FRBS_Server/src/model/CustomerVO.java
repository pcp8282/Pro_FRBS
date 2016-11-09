package model;

public class CustomerVO {
	private String customer_id;
    private String password;
    private String phone_number;
    private String email;
    
	public CustomerVO() {}

	public CustomerVO(String customer_id, String password, String phone_number,
			String email) {
		super();
		this.customer_id = customer_id;
		this.password = password;
		this.phone_number = phone_number;
		this.email = email;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
