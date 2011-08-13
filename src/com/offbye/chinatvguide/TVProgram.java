package com.offbye.chinatvguide;

public class TVProgram {
	private String id=null;
	private String channel=null;
	private String date=null;
	private String starttime=null;
	private String endtime=null;
	private String program=null;
	private String daynight=null;

	private String channelname=null;

	public TVProgram() {
		super();
	}

	public TVProgram(String id, String channel, String date, String starttime, String endtime,
			String program, String daynight) {
		super();
		this.id = id;
		this.channel = channel;
		this.date = date;
		this.starttime = starttime;
		this.endtime = endtime;
		this.program = program;
		this.daynight = daynight;
	}

	public TVProgram(String id, String channel, String date, String starttime, String endtime,
			String program, String daynight, String channelname) {
		super();
		this.id = id;
		this.channel = channel;
		this.date = date;
		this.starttime = starttime;
		this.endtime = endtime;
		this.program = program;
		this.daynight = daynight;
		this.channelname = channelname;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((daynight == null) ? 0 : daynight.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		result = prime * result
				+ ((starttime == null) ? 0 : starttime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TVProgram other = (TVProgram) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (daynight == null) {
			if (other.daynight != null)
				return false;
		} else if (!daynight.equals(other.daynight))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (program == null) {
			if (other.program != null)
				return false;
		} else if (!program.equals(other.program))
			return false;
		if (starttime == null) {
			if (other.starttime != null)
				return false;
		} else if (!starttime.equals(other.starttime))
			return false;
		return true;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getDaynight() {
		return daynight;
	}
	public void setDaynight(String daynight) {
		this.daynight = daynight;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getChannelname() {
		if(channelname!=null){
			return channelname;
		}
		else{
			return "";
		}
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

}
