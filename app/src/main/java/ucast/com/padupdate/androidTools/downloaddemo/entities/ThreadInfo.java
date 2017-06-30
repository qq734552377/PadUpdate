package ucast.com.padupdate.androidTools.downloaddemo.entities;

/**
 * 线程信息实体类
 *
 */
public class ThreadInfo {
	private int id;

	/**
	 * 跟下载的文件的url一致
	 */
	private String url;

	/**
	 * 上次保存的文件的下载进度
	 */
	private int start;

	/**
	 * 代表的线程中下载文件的总长度
	 */
	private int end;

	/**
	 * 下载的进度（即文件下载到了哪儿，以字节数为单位）
	 */
	private int finished;

	public ThreadInfo() {
	}

	public ThreadInfo(int id, String url, int start, int end, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", end=" + end + ", finished=" + finished + "]";
	}
}