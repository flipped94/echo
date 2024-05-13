package org.example.echo.interact.service;

import jakarta.annotation.Resource;
import org.example.echo.interact.repository.InteractRepository;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.interact.GetByIdsRequest;
import org.example.echo.sdk.interact.Interact;
import org.example.echo.sdk.interact.InteractReq;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteractService {

    @Resource
    private InteractRepository interactRepository;

    public void incrReadCount(InteractReq interactReq) {
        interactRepository.incrReadCount(interactReq.getBiz(), interactReq.getBizId());
    }

    public void incrCommentCount(InteractReq interactReq) {
        interactRepository.incrCommentCount(interactReq.getBiz(), interactReq.getBizId());
    }

    public void like(InteractReq interactReq) {
        interactRepository.incrLikeCount(interactReq.getBiz(), interactReq.getBizId(), LoginUserContext.getUserId());
    }

    public void cancelLike(InteractReq interactReq) {
        interactRepository.decrLikeCount(interactReq.getBiz(), interactReq.getBizId(), LoginUserContext.getUserId());
    }

    public void collect(InteractReq interactReq, Long cid) {
        interactRepository.incrCollectCount(interactReq.getBiz(), interactReq.getBizId(), LoginUserContext.getUserId(), cid);
    }

    public void cancelCollect(InteractReq interactReq) {
        interactRepository.decrCollectCount(interactReq.getBiz(), interactReq.getBizId(), LoginUserContext.getUserId());
    }

    public Interact get(InteractReq interactReq) {
        final Interact interact = interactRepository.get(interactReq.getBiz(), interactReq.getBizId());
        interact.setLiked(interactRepository.liked(interact.getBiz(), interactReq.getBizId(), interactReq.getUserId()));
        interact.setCollected(interactRepository.collected(interact.getBiz(), interactReq.getBizId(), interactReq.getUserId()));
        return interact;
    }

    public List<Interact> getByBizAndIds(GetByIdsRequest request) {
        List<Interact> interacts = interactRepository.getByBizAndIds(request.getBiz(), request.getIds());
        interacts.forEach(interact -> {
            interact.setLiked(interactRepository.liked(interact.getBiz(), interact.getBizId(), request.getUserId()));
            interact.setCollected(interactRepository.collected(interact.getBiz(), interact.getBizId(), request.getUserId()));
        });
        return interacts;
    }

    public List<Long> getTopK() {
        return interactRepository.getTopK();
    }

}
