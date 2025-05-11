package com.sip.interfaces;

import java.util.List;

import com.sip.entities.User;
import com.sip.requests.RegisterRequest;
import com.sip.responses.RegisterResponse;


public interface RegisterService {

	RegisterResponse addUser(RegisterRequest request);

	void deleteUserNotification(long id);

	int nombresNotifications();

	List<User> listUsers();

	RegisterResponse activateUser(long id);

	List<User> getUsers();

	RegisterResponse updateUser(RegisterRequest request);

	

}
