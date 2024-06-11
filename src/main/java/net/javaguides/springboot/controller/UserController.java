package net.javaguides.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.http.HttpEntity;
import net.javaguides.springboot.entity.SalesforceLogin;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	private final Logger logger = LogManager.getLogger(UserController.class);

	// get all users
	@GetMapping
	public List<User> getAllUsers() {
		logger.info("get all users API Started");
		logger.info("Info level log message");
        logger.debug("Debug level log message");
        logger.error("Error level log message");
		List<User> userList = this.userRepository.findAll();
		logger.info("get all users API Ends");
		return userList;
	}

	// get user by id
	@GetMapping("/{id}")
	public User getUserById(@PathVariable (value = "id") long userId) {
		logger.info("get user by id API Started");
		return this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId));
	}

	// create user
	@PostMapping
	public User createUser(@RequestBody User user) {
		logger.info("User creation API Started");
		User userVal =  this.userRepository.save(user);
		logger.info("User creation API Ends");
		return userVal;
	}
	
	// update user
	@PutMapping("/{id}")
	public User updateUser(@RequestBody User user, @PathVariable ("id") long userId) {
		 User existingUser = this.userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId));
		 existingUser.setFirstName(user.getFirstName());
		 existingUser.setLastName(user.getLastName());
		 existingUser.setEmail(user.getEmail());
		 return this.userRepository.save(existingUser);
	}
	
	// delete user by id
	@DeleteMapping("/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable ("id") long userId){
		 User existingUser = this.userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId));
		 this.userRepository.delete(existingUser);
		 return ResponseEntity.ok().build();
	}

	public SalesforceLogin getSalesforceToken()
			throws ClientProtocolException, IOException {
		// DefaultHttpClient httpclient = new DefaultHttpClient();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://login.salesforce.com/services/oauth2/token");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", "requestObj.getClient_id()"));
		params.add(new BasicNameValuePair("client_secret", "requestObj.getClient_secret()"));
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("username", "requestObj.getUsername()"));
		params.add(new BasicNameValuePair("password", "requestObj.getPassword()"));
		post.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = httpclient.execute(post);
		SalesforceLogin salesforceLogin = null;
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				//salesforceLogin = new Gson().fromJson(result, SalesforceLogin.class);
			}
		} catch (Exception e) {
			logger.error(e.getMessage().toString());
			
		}
		return salesforceLogin;
	}
}
