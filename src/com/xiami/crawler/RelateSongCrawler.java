package com.xiami.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dao.Dao;
import dao.RelateSongInfoDao;
import dao.SongInfoDao;



public class RelateSongCrawler {
	
	//HashSet<String> crawledSongs;
	Dao dao;
	RelateSongInfo rInfo;
	RelateSongInfoDao rDao;
	
	public RelateSongCrawler() {
		// TODO Auto-generated constructor stub
		
	}
	
	/*public void initial() throws ClassNotFoundException{
		crawledSongs = new HashSet<String>();
		sDao = new SongInfoDao();
		ArrayList<Map<String, String>> songs = sDao.getResult("sname");
		for (int i = 0; i < songs.size(); i++) {
			crawledSongs.add(songs.get(i).get(1));
		}
	}*/
	
	public void crawlRelateSong() throws ClassNotFoundException{
		rDao = new RelateSongInfoDao();
		ArrayList<Map<String, String>> search = rDao.getResult("id, sid", "xxsonginfoc");
		for(int i = 0; i < search.size(); i++){
			String id = search.get(i).get("id");
			String sid = search.get(i).get("sid");
			//String sname = search.get(i).get("sname");
			//String singer = search.get(i).get("singer");
			//System.out.println(singer);
			String url = "http://www.xiami.com/song/" + sid;
			System.out.println(url);
			Document doc = null;
			doc = this.getHtmlContent(url);
			//String url2 = this.getURL(doc);
			//System.out.println(url2);
			//Document doc2 = null;
			//doc2 = this.getHtmlContent(url2);
			this.getAndSaveRelateSong(doc, id, sid);
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public String getURL(Document doc){
		String url = doc.select("a[target][title]").first().attr("href");
		return url;
	}
	
	public void getAndSaveRelateSong(Document doc, String id, String sid){
		ArrayList<String> relateSong = null;
		String sname = null;
		String rsname = "";
		if (doc!=null) {
			sname = doc.select("div[id=title]").select("h1").text();
			relateSong = extractRelateSong(doc);
		}else {
			System.err.println("The html is not fetched!");
		}
		if (relateSong!=null && relateSong.size()>0) {
			for (int i = 0; i < relateSong.size(); i++) {
				rsname += relateSong.get(i);
				if(i < relateSong.size()-1)
					rsname += ",";
			}
			/*for (int i = 0; i < 3; i++) {
				rsname += relateSong.get(i);
				if(i < 2)
					rsname += ",";
			}*/
		}
		rInfo = new RelateSongInfo();
		rInfo.setId(id);
		rInfo.setSid(sid);
		rInfo.setSname(sname);
		rInfo.setRsname(rsname.trim());
		rDao.insertRelateSongInfo(rInfo);
		System.out.println(sid + sname + rsname);

	}
	
	public Document getHtmlContent(String url){
		Document doc = null;
		try {
			doc = Jsoup.connect(url)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
			.timeout(10*1000)
			.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
	
	public ArrayList<String> extractRelateSong(Document doc){
		ArrayList<String> relateSongList = new ArrayList<String>();
		Elements relateSong = doc.select("td.song_name").select("a");
		for (Element element : relateSong) {
			String relateSongName = element.text();
			relateSongList.add(relateSongName);
		}
		return relateSongList;
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		RelateSongCrawler rCrawler = new RelateSongCrawler();
		//rCrawler.initial();
		rCrawler.crawlRelateSong();
		
		
	}

}
