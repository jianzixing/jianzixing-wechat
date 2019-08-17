package com.jianzixing.webapp.service.spcard;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.spcard.TableShoppingCard;
import com.jianzixing.webapp.tables.spcard.TableShoppingCardList;
import com.jianzixing.webapp.tables.spcard.TableShoppingCardSpending;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.encryption.MD5Utils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.ExcelUtils;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.criteria.Update;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultShoppingCardService implements ShoppingCardService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addShoppingCard(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableShoppingCard.class);
        object.put(TableShoppingCard.createTime, new Date());

        int count = object.getIntValue(TableShoppingCard.count);
        if (count > 1000000) {
            throw new ModuleException(StockCode.TOO_LARGE, "每个批次购物卡最大数量不能超过100万");
        }

        String password = object.getString(TableShoppingCard.password);
        if (StringUtils.isNotBlank(password)) {
            object.put(TableShoppingCard.password, MD5Utils.md5(password));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        object.put(TableShoppingCard.number, format.format(new Date()) + RandomUtils.randomNumber(6));
        object.checkAndThrowable();
        object.put(TableShoppingCard.status, BatchStatus.NORMAL.getCode());
        sessionTemplate.save(object);
    }

    @Override
    public void updateShoppingCard(ModelObject object) throws ModelCheckerException {
        ModelObject old = sessionTemplate.get(TableShoppingCard.class, object.getIntValue(TableShoppingCard.id));
        object.remove(TableShoppingCard.createTime);
        object.remove(TableShoppingCard.number);
        object.remove(TableShoppingCard.status);
        if (old != null && old.getIntValue(TableShoppingCard.status) == BatchStatus.NORMAL.getCode()) {
            String password = object.getString(TableShoppingCard.password);
            if (StringUtils.isNotBlank(password) && password.trim() != "") {
                object.put(TableShoppingCard.password, MD5Utils.md5(password));
            }
        } else {
            object.retain(TableShoppingCard.id, TableShoppingCard.name, TableShoppingCard.detail);
        }
        object.setObjectClass(TableShoppingCard.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getShoppingCards(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableShoppingCard.class);
        query.limit(start, limit);
        query.order(TableShoppingCard.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void declareShoppingCard(int id) {
        sessionTemplate.update(
                Criteria.update(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.scid, id)
                        .value(TableShoppingCardList.status, ShoppingCardStatus.DECLARE.getCode())
        );
        sessionTemplate.update(
                Criteria.update(TableShoppingCard.class)
                        .eq(TableShoppingCard.id, id)
                        .value(TableShoppingCard.status, BatchStatus.DECLARE.getCode())
        );
    }

    @Override
    public synchronized void createShoppingCardList(int id, String scpwd) throws ModuleException {
        long count = sessionTemplate.count(
                Criteria.query(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.scid, id)
        );
        ModelObject sc = sessionTemplate.get(
                Criteria.query(TableShoppingCard.class)
                        .eq(TableShoppingCard.id, id)
        );

        if (sc.getIntValue(TableShoppingCard.status) != BatchStatus.NORMAL.getCode()) {
            throw new ModuleException(StockCode.FAILURE, "当前购物卡批次状态不允许重新创建");
        }

        String oldScpwd = sc.getString(TableShoppingCard.password);
        int createCount = sc.getIntValue(TableShoppingCard.count);
        double money = sc.getDoubleValue(TableShoppingCard.money);

        if (!oldScpwd.equals(MD5Utils.md5(scpwd))) {
            throw new ModuleException(StockCode.FAILURE, "购物卡批次密码输入错误");
        }

        sessionTemplate.update(Criteria.update(TableShoppingCard.class)
                .eq(TableShoppingCard.id, id)
                .value(TableShoppingCard.status, BatchStatus.CREATED.getCode()));

        if (count == createCount) {
            throw new ModuleException(StockCode.USING, "购物卡已经全部生成,无需重复生成");
        }

        long haveCount = createCount - count;
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        String prefix = RandomUtils.randomUpper(3);
        prefix += format.format(new Date());
        for (int i = 0; i < haveCount; i++) {
            int exec = 0;
            while (true) {
                try {
                    String cardNumber = prefix + RandomUtils.randomNumber(10);
                    String password = null;
                    while (true) {
                        password = RandomUtils.randomAlphanumericUpper(4) + "-" +
                                RandomUtils.randomAlphanumericUpper(4) + "-" +
                                RandomUtils.randomAlphanumericUpper(4) + "-" +
                                RandomUtils.randomAlphanumericUpper(4);
                        ModelObject exist = sessionTemplate.get(Criteria.query(TableShoppingCardList.class)
                                .eq(TableShoppingCardList.password, password)
                                .eq(TableShoppingCardList.status, ShoppingCardStatus.NORMAL.getCode()));
                        if (exist == null) {
                            break;
                        }
                    }

                    ModelObject scl = new ModelObject(TableShoppingCardList.class);
                    scl.put(TableShoppingCardList.scid, id);
                    scl.put(TableShoppingCardList.cardNumber, cardNumber);
                    scl.put(TableShoppingCardList.password, password);
                    scl.put(TableShoppingCardList.money, money);
                    scl.put(TableShoppingCardList.balance, money);
                    scl.put(TableShoppingCardList.status, ShoppingCardStatus.NORMAL.getCode());
                    scl.put(TableShoppingCardList.createTime, new Date());
                    sessionTemplate.save(scl);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                exec++;
                if (exec > 20) {
                    throw new ModuleException(StockCode.FAILURE, "购物卡生成失败");
                }
            }
        }
    }

    @Override
    public void exportShoppingCardList(int id, String password, OutputStream outputStream) throws Exception {
        ModelObject sc = sessionTemplate.get(
                Criteria.query(TableShoppingCard.class)
                        .eq(TableShoppingCard.id, id)
        );
        String oldScpwd = sc.getString(TableShoppingCard.password);
        if (password == null || !oldScpwd.equals(MD5Utils.md5(password))) {
            throw new ModuleException(StockCode.FAILURE, "购物卡批次密码输入错误");
        }

        final int[] start = {0};
        int limit = 1000;
        ExcelUtils.writes(outputStream, new ExcelUtils.ExcelWriteCallback() {
            @Override
            public List<ExcelUtils.SheetMate> getSheets() {
                ExcelUtils.SheetMate sheetMate = new ExcelUtils.SheetMate();
                sheetMate.setName("批次" + sc.getString(TableShoppingCard.number) + "购物卡");
                sheetMate.setLoop(true);

                List<ExcelUtils.RowMate> rowMates = new ArrayList<>();
                ExcelUtils.RowMate scid = new ExcelUtils.RowMate("批次号", "scNumber");
                rowMates.add(scid);
                ExcelUtils.RowMate cardNumber = new ExcelUtils.RowMate("卡号", "cardNumber");
                rowMates.add(cardNumber);
                ExcelUtils.RowMate password = new ExcelUtils.RowMate("密码", "password");
                rowMates.add(password);
                ExcelUtils.RowMate uid = new ExcelUtils.RowMate("用户ID", "uid");
                rowMates.add(uid);
                ExcelUtils.RowMate balance = new ExcelUtils.RowMate("余额", "balance");
                rowMates.add(balance);
                ExcelUtils.RowMate bindTime = new ExcelUtils.RowMate("绑定时间", "bindTime");
                rowMates.add(bindTime);
                ExcelUtils.RowMate useTime = new ExcelUtils.RowMate("使用时间", "useTime");
                rowMates.add(useTime);
                ExcelUtils.RowMate status = new ExcelUtils.RowMate("状态", "statusName");
                rowMates.add(status);
                ExcelUtils.RowMate createTime = new ExcelUtils.RowMate("创建时间", "createTime");
                rowMates.add(createTime);

                sheetMate.setRowMates(rowMates);

                List list = new ArrayList();
                list.add(sheetMate);
                return list;
            }

            @Override
            public List<ModelObject> getWrites(ExcelUtils.SheetMate sheet, int totalIndex, int index) throws Exception {
                List<ModelObject> objects = sessionTemplate.list(
                        Criteria.query(TableShoppingCardList.class)
                                .eq(TableShoppingCardList.scid, id)
                                .limit(start[0], limit)
                );

                if (objects != null) {
                    for (ModelObject object : objects) {
                        ShoppingCardStatus statusName = ShoppingCardStatus.get(object.getIntValue(TableShoppingCardList.status));
                        object.put("statusName", statusName != null ? statusName.getCode() : "");
                        object.put("scNumber", sc.getString(TableShoppingCard.number));
                        object.put("password", object.getString(TableShoppingCardList.password));
                    }
                }

                start[0] = start[0] + limit;
                return objects;
            }
        });
    }

    @Override
    public Paging getShoppingCardLists(ModelObject search, int id, int start, int limit) {
        Query query = Criteria.query(TableShoppingCardList.class);
        query.subjoin(TableShoppingCard.class).eq(TableShoppingCard.id, TableShoppingCardList.scid).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableShoppingCardList.uid).single();
        query.limit(start, limit);
        query.eq(TableShoppingCardList.scid, id);
        query.order(TableShoppingCardList.createTime, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void declareShoppingCardList(int id, List<String> numbers) {
        sessionTemplate.update(
                Criteria.update(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.scid, id)
                        .in(TableShoppingCardList.cardNumber, numbers)
                        .value(TableShoppingCardList.status, ShoppingCardStatus.DECLARE.getCode())
        );
    }

    @Override
    public List<ModelObject> getShoppingCardSpending(int id, String cardNumber) {
        Query query = Criteria.query(TableShoppingCardSpending.class);
        query.subjoin(TableShoppingCard.class).eq(TableShoppingCard.id, TableShoppingCardSpending.scid).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableShoppingCardSpending.uid).single();
        query.order(TableShoppingCardSpending.createTime, false);
        query.eq(TableShoppingCardSpending.scid, id);
        query.eq(TableShoppingCardSpending.cardNumber, cardNumber);
        return sessionTemplate.list(query);
    }

    @Override
    public ModelObject getShoppingCardById(int id) {
        return sessionTemplate.get(TableShoppingCard.class, id);
    }

    @Override
    public ModelObject getShoppingCardByNumber(long uid, String cardNumber) {
        return sessionTemplate.get(
                Criteria.query(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.uid, uid)
                        .eq(TableShoppingCardList.cardNumber, cardNumber)
                        .eq(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
        );
    }

    @Override
    public long orderIn(String number, String cardNumber, long uid, BigDecimal price, String msg) throws ModuleException, TransactionException {
        ModelObject card = sessionTemplate.get(Criteria.query(TableShoppingCardList.class)
                .eq(TableShoppingCardList.cardNumber, cardNumber)
                .eq(TableShoppingCardList.uid, uid));
        if (card != null) {
            int scid = card.getIntValue(TableShoppingCardList.scid);
            BigDecimal balance = card.getBigDecimal(TableShoppingCardList.balance);
            if (CalcNumber.as(balance).gte(price)) {
                String priceStr = CalcNumber.as(price).toPrice();
                BigDecimal result = balance.subtract(price);
                ModelObject spending = new ModelObject(TableShoppingCardSpending.class);
                spending.put(TableShoppingCardSpending.scid, scid);
                spending.put(TableShoppingCardSpending.type, ShoppingCardSpeedingType.SUB.getCode());
                spending.put(TableShoppingCardSpending.cardNumber, cardNumber);
                spending.put(TableShoppingCardSpending.orderNumber, number);
                spending.put(TableShoppingCardSpending.uid, uid);
                spending.put(TableShoppingCardSpending.money, priceStr);
                spending.put(TableShoppingCardSpending.detail, msg);
                spending.put(TableShoppingCardSpending.createTime, new Date());

                return sessionTemplate.execute(new TransactionCallback<Long>() {
                    @Override
                    public Long invoke(Transaction transaction) throws Exception {
                        Update update = Criteria.update(TableShoppingCardList.class)
                                .value(TableShoppingCardList.balance, result)
                                .eq(TableShoppingCardList.scid, scid)
                                .eq(TableShoppingCardList.cardNumber, cardNumber);
                        if (balance.doubleValue() == price.doubleValue()) {
                            update.value(TableShoppingCardList.status, ShoppingCardStatus.USED.getCode());
                        }
                        sessionTemplate.update(update);
                        sessionTemplate.save(spending);
                        return spending.getLongValue(TableShoppingCardSpending.id);
                    }
                });
            }
        } else {
            throw new ModuleException(StockCode.ARG_NULL, "没有找到购物卡信息");
        }
        return 0;
    }

    @Override
    public List<ModelObject> getValidShoppingCardsByUid(long uid) {
        List<ModelObject> spcards = sessionTemplate.list(Criteria.query(TableShoppingCardList.class)

                .excludes(TableShoppingCardList.password)

                .subjoin(TableShoppingCard.class).eq(TableShoppingCard.id, TableShoppingCardList.scid).single().query()
                .eq(TableShoppingCardList.uid, uid)
                .eq(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
                .gt(TableShoppingCardList.balance, 0));
        return spcards;
    }

    @Override
    public List<ModelObject> getInvalidUserShoppingCards(long uid, int page) {
        List<ModelObject> spcards = sessionTemplate.list(Criteria.query(TableShoppingCardList.class)

                .excludes(TableShoppingCardList.password)

                .subjoin(TableShoppingCard.class).eq(TableShoppingCard.id, TableShoppingCardList.scid).single().query()
                .eq(TableShoppingCardList.uid, uid)
                .ne(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
                .lte(TableShoppingCardList.balance, 0)
                .limit((page - 1) * 10, 20));
        return spcards;
    }

    @Override
    public long getInvalidUserShoppingCardsCount(long uid) {
        return sessionTemplate.count(Criteria.query(TableShoppingCardList.class)
                .eq(TableShoppingCardList.uid, uid)
                .ne(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
                .lte(TableShoppingCardList.balance, 0));
    }

    @Override
    public long getValidShoppingCardsCount(long uid) {
        return sessionTemplate.count(Criteria.query(TableShoppingCardList.class)
                .eq(TableShoppingCardList.uid, uid)
                .eq(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
                .gt(TableShoppingCardList.balance, 0));
    }

    @Override
    public void bindSpcard(long uid, String pwd) throws ModuleException {
        pwd = pwd.toUpperCase();
        ModelObject card = sessionTemplate.get(
                Criteria.query(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.password, pwd)
                        .eq(TableShoppingCardList.status, ShoppingCardStatus.NORMAL.getCode()));
        if (card != null) {
            if (card.getIntValue(TableShoppingCardList.status) == ShoppingCardStatus.NORMAL.getCode()
                    && card.isEmpty(TableShoppingCardList.uid)) {
                ModelObject update = new ModelObject(TableShoppingCardList.class);
                update.put(TableShoppingCardList.scid, card.getLongValue(TableShoppingCardList.scid));
                update.put(TableShoppingCardList.cardNumber, card.getString(TableShoppingCardList.cardNumber));
                update.put(TableShoppingCardList.uid, uid);
                update.put(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode());
                update.put(TableShoppingCardList.bindTime, new Date());
                sessionTemplate.update(update);
            } else {
                throw new ModuleException("card_bind_was", "当前购物卡已经被绑定");
            }
        } else {
            throw new ModuleException("card_empty", "当前购物卡不存在");
        }
    }

    @Override
    public List<ModelObject> getShoppingCardByNumbers(long uid, List<String> cardNumbers) {
        return sessionTemplate.list(
                Criteria.query(TableShoppingCardList.class)
                        .eq(TableShoppingCardList.uid, uid)
                        .in(TableShoppingCardList.cardNumber, cardNumbers)
                        .eq(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
        );
    }

    @Override
    public void handBack(String number, long uid, BigDecimal payPrice) throws ModuleException {
        if (payPrice.doubleValue() > 0) {
            List<ModelObject> css = sessionTemplate.list(Criteria.query(TableShoppingCardSpending.class)
                    .eq(TableShoppingCardSpending.uid, uid)
                    .eq(TableShoppingCardSpending.type, ShoppingCardSpeedingType.SUB.getCode())
                    .eq(TableShoppingCardSpending.orderNumber, number));
            if (css != null && css.size() > 0) {
                payPrice = new BigDecimal(CalcNumber.as(payPrice).toPrice());
                for (ModelObject spending : css) {
                    String cardNumber = spending.getString(TableShoppingCardSpending.cardNumber);
                    List<ModelObject> userCards = sessionTemplate.list(Criteria.query(TableShoppingCardList.class)
                            .eq(TableShoppingCardList.uid, uid)
                            .eq(TableShoppingCardList.cardNumber, cardNumber));
                    if (userCards != null) {
                        for (ModelObject userCard : userCards) {
                            int status = userCard.getIntValue(TableShoppingCardList.status);
                            if (status != ShoppingCardStatus.DECLARE.getCode()) {
                                status = ShoppingCardStatus.BIND.getCode();
                            }

                            BigDecimal money = userCard.getBigDecimal(TableShoppingCardList.money);
                            BigDecimal oldBalance = userCard.getBigDecimal(TableShoppingCardList.balance);
                            BigDecimal balance = userCard.getBigDecimal(TableShoppingCardList.balance);
                            if (payPrice.doubleValue() > money.doubleValue()) {
                                balance = money;
                                payPrice = payPrice.subtract(money);
                            } else {
                                balance = balance.add(payPrice);
                                payPrice = payPrice.subtract(payPrice);
                            }

                            // 如果超出卡余额则抛出异常
                            if (money.doubleValue() <= oldBalance.doubleValue()) {
                                throw new ModuleException("balance_out_money", "退还金额大于购物卡总金额");
                            }

                            ModelObject update = new ModelObject(TableShoppingCardList.class);
                            update.put(TableShoppingCardList.scid, userCard.getLongValue(TableShoppingCardList.scid));
                            update.put(TableShoppingCardList.cardNumber, userCard.getString(TableShoppingCardList.cardNumber));
                            update.put(TableShoppingCardList.status, status);
                            update.put(TableShoppingCardList.balance, balance);

                            ModelObject handBackLog = new ModelObject(TableShoppingCardSpending.class);
                            handBackLog.put(TableShoppingCardSpending.scid, userCard.getLongValue(TableShoppingCardList.scid));
                            handBackLog.put(TableShoppingCardSpending.type, ShoppingCardSpeedingType.ADD.getCode());
                            handBackLog.put(TableShoppingCardSpending.cardNumber, cardNumber);
                            handBackLog.put(TableShoppingCardSpending.orderNumber, number);
                            handBackLog.put(TableShoppingCardSpending.uid, uid);
                            handBackLog.put(TableShoppingCardSpending.money, balance);
                            handBackLog.put(TableShoppingCardSpending.detail,
                                    "退还订单(" + number + ")" +
                                            "使用的购物卡金额(" + CalcNumber.as(oldBalance).subtract(balance).toPrice() + ")");
                            handBackLog.put(TableShoppingCardSpending.createTime, new Date());

                            sessionTemplate.update(update);
                            sessionTemplate.save(handBackLog);

                            if (payPrice.doubleValue() <= 0) break;
                        }
                    }
                }
            } else {
                throw new ModuleException("shopping_card_spending_empty", "购物卡消费记录不存在");
            }
        }
    }

    @Override
    public long getValidSPCountByUid(long uid) {
        long count = sessionTemplate.count(Criteria.query(TableShoppingCardList.class)
                .eq(TableShoppingCardList.uid, uid)
                .eq(TableShoppingCardList.status, ShoppingCardStatus.BIND.getCode())
                .gt(TableShoppingCardList.balance, 0));
        return count;
    }
}
