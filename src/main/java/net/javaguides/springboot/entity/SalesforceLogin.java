package net.javaguides.springboot.entity;
import java.util.Objects;

public class SalesforceLogin implements EntityBase{

	private String access_token;
	private String instance_url;
	private String id;
	private String token_type;
	private String issued_at;
	private String signature;
	private String caseType;
	@Override
	public Long getId() {
		return null;
	}
	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}
	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	/**
	 * @return the instance_url
	 */
	public String getInstance_url() {
		return instance_url;
	}
	/**
	 * @param instance_url the instance_url to set
	 */
	public void setInstance_url(String instance_url) {
		this.instance_url = instance_url;
	}
	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}
	/**
	 * @param token_type the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	/**
	 * @return the issued_at
	 */
	public String getIssued_at() {
		return issued_at;
	}
	/**
	 * @param issued_at the issued_at to set
	 */
	public void setIssued_at(String issued_at) {
		this.issued_at = issued_at;
	}
	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}
	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(access_token, caseType, id, instance_url, issued_at, signature, token_type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SalesforceLogin)) {
			return false;
		}
		SalesforceLogin other = (SalesforceLogin) obj;
		return Objects.equals(access_token, other.access_token) && Objects.equals(caseType, other.caseType)
				&& Objects.equals(id, other.id) && Objects.equals(instance_url, other.instance_url)
				&& Objects.equals(issued_at, other.issued_at) && Objects.equals(signature, other.signature)
				&& Objects.equals(token_type, other.token_type);
	}
	@Override
	public String toString() {
		return "SalesforceLogin [access_token=" + access_token + ", instance_url=" + instance_url + ", id=" + id
				+ ", token_type=" + token_type + ", issued_at=" + issued_at + ", signature=" + signature + ", caseType="
				+ caseType + "]";
	}
	/**
	 * @return the caseType
	 */
	public String getCaseType() {
		return caseType;
	}
	/**
	 * @param caseType the caseType to set
	 */
	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}
	
}
