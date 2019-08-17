package com.jianzixing.webapp.service.spcard;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCardService {
    void addShoppingCard(ModelObject object) throws ModelCheckerException, ModuleException;

    void updateShoppingCard(ModelObject object) throws ModelCheckerException;

    Paging getShoppingCards(ModelObject search, int start, int limit);

    void declareShoppingCard(int id);

    void createShoppingCardList(int id, String password) throws ModuleException;

    void exportShoppingCardList(int id, String password, OutputStream outputStream) throws Exception;

    Paging getShoppingCardLists(ModelObject search, int id, int start, int limit);

    void declareShoppingCardList(int id, List<String> numbers);

    List<ModelObject> getShoppingCardSpending(int id, String cardNumber);

    ModelObject getShoppingCardById(int id);

    ModelObject getShoppingCardByNumber(long uid, String cardNumber);

    long orderIn(String number, String cardNumber, long uid, BigDecimal price, String msg)
            throws ModuleException, TransactionException;

    List<ModelObject> getValidShoppingCardsByUid(long uid);

    List<ModelObject> getInvalidUserShoppingCards(long uid, int page);

    long getInvalidUserShoppingCardsCount(long uid);

    long getValidShoppingCardsCount(long uid);

    void bindSpcard(long uid, String pwd) throws ModuleException;

    List<ModelObject> getShoppingCardByNumbers(long uid, List<String> cardNumbers);

    void handBack(String number, long uid, BigDecimal payPrice) throws ModuleException;

    long getValidSPCountByUid(long uid);
}
