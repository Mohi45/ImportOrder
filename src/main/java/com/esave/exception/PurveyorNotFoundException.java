package com.esave.exception;

public class PurveyorNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private Integer reasonCode;

	private static final String MESSAGE_SEPARATOR = " # ";
	
	private String purveyorId;
	
	private String orderId;

	/**
	 * @param detailedMsg
	 * @param reasonCd
	 */
	public PurveyorNotFoundException(String detailedMsg, Integer reasonCd) {
		super(detailedMsg);
		this.reasonCode = reasonCd;
	}

	/**
	 * @param detailedMsg
	 * @param t
	 * @param reasonCd
	 */
	public PurveyorNotFoundException(String detailedMsg, Throwable t, Integer reasonCd) {
		super(detailedMsg, t);
		this.reasonCode = reasonCd;
	}
	
	public PurveyorNotFoundException(String detailedMsg, Integer reasonCd, String purveyorId, String orderId) {
		super(detailedMsg);
		this.reasonCode = reasonCd;
		this.purveyorId = purveyorId;
		this.orderId = orderId;
	}

	public Integer getReasonCode() {
		if (this.reasonCode == null)
			return Integer.valueOf(-1);
		return this.reasonCode;
	}

	public String getDetailedMessage() {
		return super.getMessage();
	}

	public String getPurveyorId() {
		return purveyorId;
	}

	public void setPurveyorId(String purveyorId) {
		this.purveyorId = purveyorId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Messages
		sb.append("reasonCode=[").append(this.getReasonCode()).append("]");
		sb.append(MESSAGE_SEPARATOR);
		sb.append("detailedMessage=[").append(this.getDetailedMessage()).append("]");

		// Location
		StringBuilder sb2 = new StringBuilder();
		StackTraceElement[] ste = this.getStackTrace();
		if (ste != null && ste[0] != null) {
			sb2.append(MESSAGE_SEPARATOR);
			sb2.append(ste[0].getClassName());
			sb2.append(".");
			sb2.append(ste[0].getMethodName());
			sb2.append("(");
			sb2.append(ste[0].getFileName());
			sb2.append(":");
			sb2.append(ste[0].getLineNumber());
			sb2.append(")");
		}

		return sb.append(sb2).toString();

	}

}
