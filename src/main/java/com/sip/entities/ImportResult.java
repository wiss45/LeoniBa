package com.sip.entities;

public class ImportResult {
	 private String message;
	    private int count;
	    private boolean success;

	    // Constructeurs, getters, setters

	    public ImportResult(String message, int count, boolean success) {
	        this.message = message;
	        this.count = count;
	        this.success = success;
	    }

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}
	    
	    
}
