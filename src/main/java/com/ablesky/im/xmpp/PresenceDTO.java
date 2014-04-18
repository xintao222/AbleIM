package com.ablesky.im.xmpp;

import java.io.Serializable;

import org.jivesoftware.smack.packet.Presence;

public class PresenceDTO implements Serializable {
	private String from;
	private String to;
	private String status;
	private String type;

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return this.from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return this.to;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public static PresenceDTO toDTO(Presence presence) {
		PresenceDTO dto = new PresenceDTO();
		dto.setFrom(presence.getFrom());
		dto.setTo(presence.getTo());
		dto.setStatus(presence.getStatus());
		dto.setType(presence.getType().name());

		return dto;
	}
}
