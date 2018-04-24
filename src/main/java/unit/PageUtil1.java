package unit;

import java.util.ArrayList;
import java.util.List;

public class PageUtil1<T> {

	/* 参数需要页面传入 */
	private Integer pageSize = 10;// 每页显式多少条记录
	private Integer currentPage = 1;// 当前页号

	/* 参数需要从数据查询 */
	private Integer allRowsAmount = 0;// 总记录数
	private List<T> items;// 记录集合

	/* 这些参数由计算得出 */
	private Integer allPageAmount;// 总页数
	private Integer currentPageStartRow = 1;// 当前页面的开始行
	private Integer currentPageEndRow;// 当前页面的结束行
	private Integer firstPage = 1;// 首页的页号
	private Integer lastPage;// 末页的页号
	private Integer prevPage;// 上一页页号
	private Integer nextPage;// 下一页页号
	private Integer startPageNum;// 导航开始页号
	private Integer endPageNum;// 导航结束页号
	private Integer maxPageAmount = 10;// 最多显示多少页
	public List<Integer> showPageNums = new ArrayList<Integer>();// 要显示的页号

	public PageUtil1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* 设置当前页 */
	public void setCurrentPage(int currentPage) {
		if (currentPage < 1) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
	}

	/* 设置每页记录数，默认10条 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/* 设置总记录数 */
	public void setAllRowsAmount(int allRowsAmount) {
		this.allRowsAmount = allRowsAmount;
	}

	/* 设置分页内容 */
	public void setItems(List<T> items) {
		this.items = items;
	}

	/* 设置导航页数量 */
	public void setMaxPageAmount(int maxPageAmount) {
		this.maxPageAmount = maxPageAmount;
	}

	public void calculatePage() {
		// 计算总页数
		if (this.allRowsAmount % this.pageSize == 0) {
			this.allPageAmount = this.allRowsAmount / this.pageSize;
		} else {
			this.allPageAmount = this.allRowsAmount / this.pageSize + 1;
		}
		// 设置首页
		this.firstPage = 1;
		// 设置末页
		this.lastPage = this.allPageAmount;
		if (this.currentPage * pageSize < allRowsAmount) {
			this.currentPageEndRow = this.currentPage * pageSize;
			this.currentPageStartRow = (this.currentPage - 1) * pageSize + 1;
		} else {
			this.currentPageEndRow = this.allRowsAmount;
			this.currentPageStartRow = (this.allPageAmount - 1) * pageSize + 1;
			if (this.currentPageStartRow < 0) {
				this.currentPageStartRow = 0;
			}
		}
		// 设置前一页
		if (this.currentPage > 1) {
			this.prevPage = this.currentPage - 1;
		} else {
			this.prevPage = this.currentPage;
		}
		// 设置下一页
		if (this.currentPage < this.lastPage) {
			this.nextPage = this.currentPage + 1;
		} else {
			this.nextPage = this.lastPage;
		}
		// 计算数字导航页
		startPageNum = Math.max(this.currentPage - maxPageAmount / 2, firstPage);
		endPageNum = Math.min(startPageNum + maxPageAmount, lastPage);
		if (endPageNum - startPageNum < maxPageAmount) {
			startPageNum = Math.max(endPageNum - maxPageAmount, 1);
		}
		for (int i = startPageNum; i <= endPageNum; i++) {
			showPageNums.add(i);
		}
	}

	// 以下get方法是对外提供的方法用来获取参数值
	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public Integer getAllRowsAmount() {
		return allRowsAmount;
	}

	public List<?> getItems() {
		return items;
	}

	public Integer getAllPageAmount() {
		return allPageAmount;
	}

	public Integer getCurrentPageStartRow() {
		return currentPageStartRow;
	}

	public Integer getCurrentPageEndRow() {
		return currentPageEndRow;
	}

	public Integer getFirstPage() {
		return firstPage;
	}

	public Integer getLastPage() {
		return lastPage;
	}

	public Integer getPrevPage() {
		return prevPage;
	}

	public Integer getNextPage() {
		return nextPage;
	}

	public Integer getStartPageNum() {
		return startPageNum;
	}

	public Integer getEndPageNum() {
		return endPageNum;
	}

	public Integer getMaxPageAmount() {
		return maxPageAmount;
	}

	public List<Integer> getShowPageNums() {
		return showPageNums;
	}

	@Override
	public String toString() {
		return "PageUtil [pageSize=" + pageSize + ", currentPage=" + currentPage + ", allRowsAmount=" + allRowsAmount + ", 每页内容items=" + items + ", allPageAmount=" + allPageAmount + ", currentPageStartRow=" + currentPageStartRow + ", currentPageEndRow=" + currentPageEndRow + ", firstPage=" + firstPage + ", lastPage=" + lastPage + ", prevPage=" + prevPage + ", nextPage=" + nextPage + ", startPageNum=" + startPageNum + ", endPageNum=" + endPageNum + ", maxPageAmount=" + maxPageAmount + ", 页号list=" + showPageNums + "]";
	}

	// ckd
	public List<T> getPageList() {

		ArrayList<T> arrayList = new ArrayList<T>();
		if (items != null && items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				if (i >= currentPageStartRow - 1 && i <= currentPageEndRow - 1) {
					T t = items.get(i);
					arrayList.add(t);
				}
			}
		}
		return arrayList;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		List<String> items = new ArrayList<String>();
		for (int i = 1; i <= 10; i++) {
			items.add("str" + i);
		}
		@SuppressWarnings("rawtypes")
		PageUtil1 pageUtil = new PageUtil1();
		pageUtil.setItems(items);
		pageUtil.setCurrentPage(3);
		pageUtil.setPageSize(3);
		pageUtil.setAllRowsAmount(items.size());
		pageUtil.calculatePage();
		List pageList = pageUtil.getPageList();
		System.out.println(pageList);
	}

	public static <T> List<T> page(List<T> items, int idx, int pagesize) {
		PageUtil1<T> pageUtil = new PageUtil1<T>();
		pageUtil.setItems(items);
		pageUtil.setCurrentPage(idx);
		pageUtil.setPageSize(pagesize);
		pageUtil.setAllRowsAmount(items.size());
		pageUtil.calculatePage();
		List<T> pageList = pageUtil.getPageList();
		return pageList;
	}
}