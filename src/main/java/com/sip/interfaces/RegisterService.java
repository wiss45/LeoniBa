package com.sip.interfaces;

import com.sip.requests.RegisterRequest;
import com.sip.responses.RegisterResponse;


public interface RegisterService {

	RegisterResponse addUser(RegisterRequest request);

}
