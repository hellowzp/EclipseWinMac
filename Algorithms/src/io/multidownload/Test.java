package io.multidownload;

import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		
		DownloadManager downloadManager = DownloadManager.getInstance();
		DownloadMission mission = null;
		String qQString = "https://doc-0pcre-1jege-s-googleusercontent.commondatastorage.googleapis.com/gs/jo9pie9r0s58uef009i9rta9a93t85o4/eho605cqj3t9th3l1lqpbqccm7b8d006/1442043000000/takeout-eu/00865570804078880484/Cgp0YWtlb3V0LWV1EnAyMDE1MDkxMlQwNzU3NTRaLzg1OTkxMjkxMzg5ODMzMDY2ODEvYzI4NjIyNjctNTkzMS00M2RiLThmOTQtYThlNTQ1OTU5NTU0LzEvNzViNGUzNjItNDEzOC00N2NjLTk2YjctYjQyNzkzODEwYWM3?a=AGjQbs7thv7m_KQrsP9XMXAG25An6oDZrXR07Inajz__4-7rhfy4rBNkFYfJmOEkG9Rx-j0qCaUHnwEbuN8HKLPAQovl_A8oiJTkUYeOB_aIG_ZQWzqXUTlb1uF4gxlgC09I2a0ZcTUIE5fkAo_habCJvwlNdnpWkVcx7KXpeg2wDpQF3SmiRIQSBJjNvhXFknzPefJ7p1_uybImwpVgLxSI5VlGz5bbeyuw7yz4Cb0F8jsA23PUlZPi25CCfUcPS-nICJqO39VDr5HhMOWIEws5Sor5BG9ye9E8mu1D-tgDwqKsxkoGbBT7zhBhnZke7nCSamo7-xofKVFfPgHhjKtS-o_NKUrm2zYS49E-MlM1inu_llicIEWHGLm_LQrcmoZ7v3kSLsfVbh3kgrNJYeS8p-Uz7sX78J9M5r03hyzLN0OvsfJbmL-h9llgMmrvD6PgrjYoByF8ymJOHIxaXnAUJOx2acZcBeC8Ya4CDThIFOOOzERU1YIkBgHnxyLXXi_T7YC-COoihIgagCbKzjAW3W-mXW7ov1ZNsLjMrmr1CGww9ML9s5svNjcCB_TYHXcIHqdu0o_5X1GvfI7NOyBjLzBdmmC8d9rSgg9FqgFFPag8W9A9AyM99AJpYTarhFlVCjMFjrujnuG3dMepiHUbd4n-4-BoR6byvD0jHcBucL4Vw8SRxkT-KiIfMNQLvFriTEgFKw703gIu8bYLAkOvnupx5nDe2lJH9oEBBQ9HdGd9dZxUNGAVVHzTB1mG2XmHNh-iwVZ0WOti3wMvdTG0WHZjXy_T4WcQGRfbCyDuU85omVGxXbQybSFx5D4b2S7vbeHrqFx0";
		String saveFileName = "3.zip";
		String saveDirectory = "e:\\Photos";
		
		try {
			mission = new DownloadMission(qQString,saveDirectory, saveFileName);
			downloadManager.addMission(mission);		
			downloadManager.start();			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
