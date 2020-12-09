package cn.idachain.finance.batch.common.model.page;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
    /**
     * 总共多少页
     */
    private Integer pageSize = 1;

    /**
     * 当前页，第一页是1,pagebound传入的page，1和0都是返回第一页
     *
     * @Override public int getOffset() {
     * if(page >= 1){
     * return (page-1) * limit;
     * }
     * return 0;
     * }
     */
    private Integer currPage = 0;

    /**
     * 总共行数
     */
    private Long totalRows = 0L;

    /**
     * 单页行数，即分页大小
     */
    private Integer pageRows = 0;

    public Pagination(Page page) {
        if (page != null) {
            this.pageSize = page.getPages();
            this.currPage = page.getCurrent();
            this.totalRows = Long.valueOf(page.getTotal());
            //dao的pageSize（每页显示的条数）对应当前属性pageRows
            this.pageRows =page.getSize();
        }
    }
}
