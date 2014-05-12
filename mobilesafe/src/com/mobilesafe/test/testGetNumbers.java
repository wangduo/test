package com.mobilesafe.test;

import java.util.List;



import android.test.AndroidTestCase;

import com.mobilesafe.bean.ContactBean;
import com.mobilesafe.service.ContactInfoService;

public class testGetNumbers extends AndroidTestCase {

	public void testGetContact() throws Exception{
		 ContactInfoService service = new ContactInfoService(getContext());
		 List<ContactBean> list = service.getContact();
		 System.out.println(list.get(0).getContactName());
//		 android.test.runner
	 }
}
