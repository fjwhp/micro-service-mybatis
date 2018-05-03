package aljoin.act.service;

import aljoin.act.iservice.ActRuActivityDefinitionService;
import aljoin.act.iservice.ActRuntimeActivityDefinitionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：wangj
 *
 * @date：2018年03月19日
 */
@Service
public class ActRuntimeActivityDefinitionServiceImpl implements ActRuntimeActivityDefinitionService {
    private static List<ActRuActivityDefinitionService> ruDeflist = new ArrayList<>();

    @Override
    public List<ActRuActivityDefinitionService> list() {
        return ruDeflist;
    }

    @Override
    public void removeAll() {
        ruDeflist.clear();
    }

    @Override
    public void save(ActRuActivityDefinitionService entity) {
        ruDeflist.add(entity);
    }
}
