package cn.idachain.finance.batch.common.model.page;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    /**
     * 分页信息
     */
    private Pagination pagination;

    /**
     * 数据列表
     */
    private List<T> list;

    public PageData(List<T> list, Page<T> page) {
        this.list = list;
        this.pagination = new Pagination(page);
    }

}
