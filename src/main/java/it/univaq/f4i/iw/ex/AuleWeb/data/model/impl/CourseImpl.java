package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class CourseImpl extends DataItemImpl<Integer> implements Course{
	private String courseName;
	
	public CourseImpl() {
		super();
		courseName = "";
	}

	@Override
	public String getCourseName() {
		return courseName;
	}

	@Override
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	
}
