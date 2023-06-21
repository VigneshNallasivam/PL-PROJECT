package com.intelizign.pl.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse 
{
	private String message;
	private Object object;
	public MessageResponse(String message) 
	{
		this.message = message;
	}
	public MessageResponse(String message, Object object)
	{
		this.message = message;
		this.object = object;
	}
}
