package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.BalanceOrg;
import cn.idachain.finance.batch.common.mapper.BalanceOrgMapper;
import cn.idachain.finance.batch.service.dao.IBalanceOrgDao;
import com.baomidou.mybatisplus.entity.Column;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BalanceOrgDao implements IBalanceOrgDao {

    @Autowired
    private BalanceOrgMapper balanceOrgMapper;

    @Override
    public int updateBalance(BalanceOrg balanceOrg, BigDecimal amount){
        EntityWrapper<BalanceOrg> condition = new EntityWrapper<BalanceOrg>();
        condition.eq("account_no",balanceOrg.getAccountNo());
        condition.eq("balance",balanceOrg.getBalance());
        condition.eq("currency",balanceOrg.getCurrency());

        BalanceOrg newBalance = new BalanceOrg();
        newBalance.setBalance(amount);
        return balanceOrgMapper.update(newBalance,condition);
    }

    @Override
    public int updateFreeze(BalanceOrg balanceOrg, BigDecimal amount){
        EntityWrapper<BalanceOrg> condition = new EntityWrapper<BalanceOrg>();
        condition.eq("account_no",balanceOrg.getAccountNo());
        condition.eq("currency",balanceOrg.getCurrency());
        condition.eq("freeze",balanceOrg.getFreeze());

        BalanceOrg newBalance = new BalanceOrg();
        newBalance.setFreeze(amount);
        return balanceOrgMapper.update(newBalance,condition);
    }

    @Override
    public BalanceOrg getBalance(String accountNo,String currency){
        BalanceOrg condition = new BalanceOrg();
        condition.setAccountNo(accountNo);
        condition.setCurrency(currency);

        return balanceOrgMapper.selectOne(condition);
    }

    @Override
    public int saveBalanceOrg(BalanceOrg balanceOrg){
        return balanceOrgMapper.insert(balanceOrg);
    }

    @Override
    public Map<String, BigDecimal> getAllCcyBalance() {
        return balanceOrgMapper.getBalanceGroupByCcy().stream()
                .collect(Collectors.toMap(BalanceOrg::getCurrency, BalanceOrg::getBalance));
    }

    @Override
    public List<BalanceOrg> getBalanceSpecial(Collection<String> accounts) {
        EntityWrapper<BalanceOrg> condition = new EntityWrapper<>();
        condition.setSqlSelect(
                Column.create().column("account_no"),
                Column.create().column("currency"),
                Column.create().column("balance")
        );
        condition.in("account_no", accounts);
        return balanceOrgMapper.selectList(condition);
    }
}
