package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoodsGroup;
import com.jianzixing.webapp.tables.goods.TableGoodsGroupSupport;
import com.jianzixing.webapp.tables.support.TableSupport;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultGoodsGroupService implements GoodsGroupService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addGroup(ModelObject object) throws ModuleException {
        object.setObjectClass(TableGoodsGroup.class);
        this.isGroupListDiffAndPut(object);

        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            throw new ModuleException(e);
        }

        String name = object.getString(TableGoodsGroup.name);
        if (name.indexOf(",") >= 0) {
            throw new ModuleException(StockCode.NOT_ENOUGH, "分组名称不能包含逗号");
        }

        sessionTemplate.save(object);
        this.saveGroupSupports(object);

        this.checkGroupNeedUpdate();
    }

    private void saveGroupSupports(ModelObject object) {
        int groupId = object.getIntValue(TableGoodsGroup.id);
        if (object.isNotEmpty("supports")) {
            String supportStr = object.getString("supports");
            String[] s1 = supportStr.split(",");
            List<ModelObject> supports = GlobalService.supportService.getSupportByArray(Arrays.asList(s1));
            if (supports != null) {
                List<ModelObject> groupSupports = new ArrayList<>();
                for (ModelObject support : supports) {
                    ModelObject gs = new ModelObject(TableGoodsGroupSupport.class);
                    gs.put(TableGoodsGroupSupport.groupId, groupId);
                    gs.put(TableGoodsGroupSupport.supportId, support.getIntValue(TableSupport.id));
                    gs.put(TableGoodsGroupSupport.supportName, support.getString(TableSupport.name));
                    groupSupports.add(gs);
                }
                sessionTemplate.delete(Criteria.delete(TableGoodsGroupSupport.class).eq(TableGoodsGroupSupport.groupId, groupId));
                sessionTemplate.save(groupSupports);
            }
        }
    }

    private boolean isGroupListDiffAndPut(ModelObject object) {
        int pid = object.getIntValue(TableGoodsGroup.pid);
        String oldIds = object.getString(TableGoodsGroup.list);
        String oldListName = object.getString(TableGoodsGroup.listName);
        if (pid == 0) {
            if ("0".equals(oldIds) && ",".equals(oldListName)) {
                return false;
            }
            object.put(TableGoodsGroup.list, "0");
            object.put(TableGoodsGroup.listName, ",");
            return true;
        }
        List<ModelObject> groups = this.getParentGroups(pid);
        if (groups != null) {
            Collections.reverse(groups);
            Iterator<ModelObject> iterator = groups.iterator();
            String ids = "0";
            String names = ",";
            while (iterator.hasNext()) {
                ModelObject g = iterator.next();
                ids += g.getString(TableGoodsGroup.pid);
                names += g.getString(TableGoodsGroup.name);
                if (iterator.hasNext()) {
                    ids += ",";
                    names += ",";
                }
            }

            object.put(TableGoodsGroup.list, ids);
            object.put(TableGoodsGroup.listName, names);
            if (!ids.equals(oldIds)) {
                return true;
            }
            if (!names.equals(oldListName)) {
                return true;
            }
        }
        return false;
    }

    private void checkGroupNeedUpdate(List<ModelObject> groups) {
        if (groups != null) {
            for (ModelObject g : groups) {
                if (this.isGroupListDiffAndPut(g)) {
                    ModelObject updateGroup = new ModelObject(TableGoodsGroup.class);
                    updateGroup.put(TableGoodsGroup.id, g.getIntValue(TableGoodsGroup.id));
                    updateGroup.put(TableGoodsGroup.list, g.getString(TableGoodsGroup.list));
                    updateGroup.put(TableGoodsGroup.listName, g.getString(TableGoodsGroup.listName));
                    sessionTemplate.update(updateGroup);
                }
            }
        }
    }

    @Override
    public void deleteGroups(List<Integer> ids) throws ModuleException {
        for (int id : ids) {
            boolean has = GlobalService.goodsService.isUsedGid(id);
            if (has) {
                throw new ModuleException(StockCode.USING, "商品分类已经被使用不允许删除");
            }

            List<ModelObject> children = sessionTemplate.query(TableGoodsGroup.class)
                    .eq(TableGoodsGroup.pid, id)
                    .queries();
            if (children != null) {
                this.deleteChildGroups(children);
            }
        }

        sessionTemplate.delete(TableGoodsGroup.class)
                .in(TableGoodsGroup.id, ids)
                .delete();

        this.checkGroupNeedUpdate();
    }

    private void deleteChildGroups(List<ModelObject> childs) throws ModuleException {
        for (ModelObject child : childs) {
            int pid = child.getIntValue(TableGoodsGroup.id);
            List<ModelObject> ch = sessionTemplate.query(TableGoodsGroup.class)
                    .eq(TableGoodsGroup.pid, pid)
                    .queries();
            if (ch != null) {
                this.deleteChildGroups(ch);
            }
            boolean has = GlobalService.goodsService.isUsedGid(pid);
            if (has) {
                throw new ModuleException(StockCode.USING, "商品分类已经被使用不允许删除");
            }
            sessionTemplate.delete(TableGoodsGroup.class, pid);
        }
    }

    @Override
    public void updateGroup(ModelObject object) throws ModuleException {
        object.setObjectClass(TableGoodsGroup.class);
        boolean isDiff = this.isGroupListDiffAndPut(object);
        try {
            object.checkUpdateThrowable();
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            throw new ModuleException(e);
        }

        String name = object.getString(TableGoodsGroup.name);
        if (name.indexOf(",") >= 0) {
            throw new ModuleException(StockCode.NOT_ENOUGH, "分组名称不能包含逗号");
        }

        sessionTemplate.update(object);
        this.saveGroupSupports(object);
        if (isDiff) {
            this.checkGroupNeedUpdate();
        }
    }

    @Override
    public List<ModelObject> getGroups() {
        List<ModelObject> groups = sessionTemplate.query(TableGoodsGroup.class)
                .subjoin(TableGoodsGroupSupport.class).eq(TableGoodsGroupSupport.groupId, TableGoodsGroup.id).query()
                .order(TableGoodsGroup.pos, true)
                .queries();
        // this.checkGroupNeedUpdate(groups);
        return ModelUtils.getListToTree(groups, TableGoodsGroup.id, TableGoodsGroup.pid, "children");
    }

    @Override
    public List<ModelObject> getListGroups() {
        List<ModelObject> groups = sessionTemplate.query(TableGoodsGroup.class)
                .order(TableGoodsGroup.pos, true)
                .queries();
        // this.checkGroupNeedUpdate(groups);
        return groups;
    }

    @Override
    public void checkGroupNeedUpdate() {
        List<ModelObject> groups = sessionTemplate.query(TableGoodsGroup.class)
                .order(TableGoodsGroup.pos, true)
                .queries();
        this.checkGroupNeedUpdate(groups);
    }

    @Override
    public List<ModelObject> getParentGroups(int gid) {
        ModelObject object = sessionTemplate.query(TableGoodsGroup.class)
                .eq(TableGoodsGroup.id, gid).query();
        if (object != null) {
            int pid = object.getIntValue(TableGoodsGroup.pid);
            List<ModelObject> parent = new ArrayList();
            parent.add(object);
            if (pid > 0) {
                List<ModelObject> list = this.getParentGroups(pid);
                if (list != null) {
                    parent.addAll(list);
                }
            }
            return parent;
        }
        return null;
    }

    @Override
    public List<ModelObject> getGroups(List<Integer> ids) {
        List<ModelObject> groups = sessionTemplate.list(Criteria.query(TableGoodsGroup.class).in(TableGoodsGroup.id, ids));
        this.checkGroupNeedUpdate(groups);
        return groups;
    }

    @Override
    public ModelObject getGroupById(long gid) {
        ModelObject group = sessionTemplate.get(Criteria.query(TableGoodsGroup.class).eq(TableGoodsGroup.id, gid));
        this.checkGroupNeedUpdate(Arrays.asList(group));
        return group;
    }

    @Override
    public List<ModelObject> getChildrenGroups(int id) {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableGoodsGroup.class).eq(TableGoodsGroup.pid, id));
        if (objects != null) {
            List<ModelObject> children = new ArrayList();
            for (ModelObject object : objects){
                children.add(object);
                int childId = object.getIntValue(TableGoodsGroup.id);
                if (childId > 0) {
                    List<ModelObject> list = this.getChildrenGroups(childId);
                    if (list != null) {
                        children.addAll(list);
                    }
                }
            }
            return children;
        }
        return null;
    }
}
