package com.xiami.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xiami.crawler.SongInfoCrawler;
import com.xiami.crawler.SongInfo;

import dao.Dao;
import dao.SongInfoDao;

public class SongInfoCrawler {
	SongInfo sInfo;
	Dao dao;
	SongInfoDao sDao;
	private HashSet<String> crawledSongs;
	public SongInfoCrawler() {
		// TODO Auto-generated constructor stub
		
	}
	public void initial() throws ClassNotFoundException{
		crawledSongs = new HashSet<String>();
		dao = new Dao();
		ArrayList<Map<String, String>> songs = dao.getResult("sid", "xxsonginfoc");
		for (int i = 0; i < songs.size(); i++) {
			crawledSongs.add(songs.get(i).get("sid"));
		}
	}
	public void crawlSongInfo() throws ClassNotFoundException, URISyntaxException{
		sDao = new SongInfoDao();
		sInfo = new SongInfo();
		ArrayList<Map<String, String>> search = sDao.getResult("*", "xxsonginfos");
		for(int i = 0; i < search.size(); i++){
			String id = search.get(i).get("id");
			String sname = search.get(i).get("sname").trim().replace(" ", "+");
			String singer = search.get(i).get("singer").trim().replace(" ", "+");
			String album = search.get(i).get("album").trim().replace(" ", "+");
			System.out.println(id+sname+singer+album);
			String url = "http://www.xiami.com/search?key=" + sname + "+" + singer + "+" + album + "&pos=1";
			System.out.println(url);
			Document doc = null;
			doc = this.getHtmlContent(url);
			String url2 = this.getURL(doc);
			if(url2 != null){
				System.out.println("url2:"+url2);
				try {
					String sid = url2.split("song/")[1];
					sInfo.id = id;
					sInfo.sid = sid;
					Document doc2 = null;
					doc2 = this.getHtmlContent(url2);
					this.getAndSaveSongInfo(doc2, sid, id);
					//sDao.insertSongInfo(sInfo);
				} catch (Exception e) {
					// TODO: handle exception
				}
				//System.out.println(mid);
			}
			else{
				sDao.updateSongInfo("notfound", id);
			}
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public String getURL(Document doc){
		if (doc!=null) {
			String url = doc.select("a[target][title]").first().attr("href");
			return url;
		}else {
			System.err.println("The html is not fetched!");
			return null;
		}
	}
	
	public void getAndSaveSongInfo(Document doc, String sid, String id){
		ArrayList<String> SongInfo = null;
		sInfo = new SongInfo();
		sInfo.id = id;
		if(!crawledSongs.contains(sid)){
			crawledSongs.add(sid);
			sInfo.sid = sid;
			if (doc!=null) {
				String sname = doc.select("div[id=title]").select("h1").text();
				sInfo.sname = sname;
				SongInfo = extractSongInfo(doc);
			}else {
				System.err.println("The html is not fetched!");
			}
			if (SongInfo!=null && SongInfo.size()>0) {
				sInfo.album = SongInfo.get(0);
				if(SongInfo.size()==2)
					sInfo.singer = SongInfo.get(1);
				if(SongInfo.size()==3)
					sInfo.lyricist = SongInfo.get(2);
				if(SongInfo.size()==4)
					sInfo.composer = SongInfo.get(3);
				if(SongInfo.size()==5)
					sInfo.arranger = SongInfo.get(4);
			}
			try {
				sDao = new SongInfoDao();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sDao.insertSongInfo(sInfo);
			sDao.updateSongInfo(sid, id);
		}
	}

	public Document getHtmlContent(String url){
		Document doc = null;
		while(true){
			
			try {
				doc = Jsoup.connect(url)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
						.timeout(10*1000)
						.get();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return doc;
	}
	
	public ArrayList<String> extractSongInfo(Document doc){
		ArrayList<String> SongInfoList = new ArrayList<String>();
		Elements songInfo = doc.select("div.album_relation").select("tr");
		//System.out.println("size:"+songInfo.size());
		for (Element element : songInfo) {
			//System.out.println("info:"+ element.text());
			String info = element.select("div").text();
			SongInfoList.add(info);
		}
		return SongInfoList;
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
		// TODO Auto-generated method stub
		SongInfoCrawler sCrawler = new SongInfoCrawler();
		sCrawler.initial();
		sCrawler.crawlSongInfo();			
	}
}
