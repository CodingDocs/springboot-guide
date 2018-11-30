package top.snailclimb.db2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.snailclimb.bean.Money;
import top.snailclimb.bean.User;
import top.snailclimb.db1.dao.UserDao;
import top.snailclimb.db2.dao.MoneyDao;

@Service
public class MoneyService {
    @Autowired
    private MoneyDao moneyDao;

    /**
     * 根据名字查找用户
     */
    public Money selectMoneyById(int id) {
        return moneyDao.findMoneyById(id);
    }

}
