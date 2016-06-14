package com.xiami.crawler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xiami.crawler.ReviewCrawler;
import com.xiami.crawler.ReviewInfo;

import dao.ReviewInfoDao;

public class ReviewCrawler {
	ReviewInfo rInfo;
	ReviewInfoDao rDao;
	public ReviewCrawler() {
		// TODO Auto-generated constructor stub
	}
	public void crawlReview() throws ClassNotFoundException, URISyntaxException{
		rDao = new ReviewInfoDao();
		ArrayList<Map<String, String>> search = new ArrayList<Map<String, String>>();
		search = rDao.getResult("id, sid", "xxsonginfoc");
		for(int i = 0; i < search.size(); i++){
			String id = search.get(i).get("id");
			String sid = search.get(i).get("sid");
			String url = "http://www.xiami.com/commentlist/turnpage/id/" + sid + "/page/1/ajax/1?type=4";
			System.out.println(url);
			Document doc = null;
			doc = this.getHtmlContent(url);
			this.getAndSaveReview(doc, id, sid);
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void getAndSaveReview(Document doc, String id, String sid) throws ClassNotFoundException{
		if (doc!=null) {
			this.extractReviewInfo(doc, id, sid);
			String url;
			while (!doc.select("div.all_page").select("a.p_redirect_l").isEmpty()) {
				url = doc.select("div.all_page").select("a.p_redirect_l").attr("href");
				url = "http://www.xiami.com" + url + "/ajax/1?type=4";
				doc = this.getHtmlContent(url);
				System.out.println(url);
				System.out.println("");
				this.extractReviewInfo(doc, id, sid);
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Error:"+e1.getMessage());
				}
			}
		}else {
			System.err.println("The html is not fetched!");
		}
	}
	private String getLocation(String uid) {
		// TODO Auto-generated method stub
		String location = "";
		Document doc = null;
		try {
			doc = Jsoup.connect(uid)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
			.timeout(60*1000)
			.ignoreContentType(true)
			.get();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("user is not exist!");
			return location;
		}
		try {
			location = doc.select("div[id=p_infoCount]").select("p").first().text();
			location = location.split("��")[1].split("��")[0];
		} catch (Exception e) {
			// TODO: handle exception
			location = "";
		}
		return location;
	}
	
	private void extractReviewInfo(Document doc, String id, String sid){
		Elements rlist = doc.select("li");
		for(Element element : rlist){
			rInfo = new ReviewInfo();
			rInfo.id = id;
			rInfo.sid = sid;
			rInfo.reviewer = element.select("span.author").select("a").first().text();
			String uid = element.select("span.author").select("a").first().attr("href");
			rInfo.location = this.getLocation(uid);
			rInfo.content = element.select("div.brief").select("div[id]").first().text().split("����iPhone")[0].split("����android")[0];
			rInfo.time = element.select("span.time").first().text();
			rDao.insertListenInfo(rInfo);
			System.out.println("id,content" + id + " " + rInfo.content);
		}
	}
	
	public Document getHtmlContent(String url){
		Document doc = null;
		while(true){
			
			try {
				doc = Jsoup.connect(url)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
				.timeout(10*1000)
				.ignoreContentType(true)
				.get();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return doc;
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
		// TODO Auto-generated method stub
		ReviewCrawler rCrawler = new ReviewCrawler();
		rCrawler.crawlReview();			
	}
}
