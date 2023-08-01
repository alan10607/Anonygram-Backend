import { Fragment, useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector, shallowEqual } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { setAllId, setArticle } from '../../../redux/actions/forum';
import { saveUserData } from '../../../redux/actions/user';
import { replySetOpen } from '../../../redux/actions/reply';
import { getJwt, getJwtPayload, isJwtValid } from '../../../service/jwt';
import { CONT_STATUS_TYPE, REPLY_BOX } from '../../../util/constant';
import ArtCont from './ArtCont';
import Cont from './Cont';
import ContDel from './Cont/ContDel';
import Reply from './Reply';
import Move from './Move';
import './index.scss';
import authRequest from '../../../service/request/authRequest';
import forumRequest from '../../../service/request/forumRequest';

export default function Art() {
  const { forum, replyId, replyIsOpen } = useSelector(state => ({

    forum: state.forum,
    replyId: state.reply.id,
    replyIsOpen: state.reply.isOpen,
    userId: state.user.userId
  }), shallowEqual);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const idList = [...forum.keys()];
  const queryLock = useRef(false);
  const querySize = 10;
  const queryPendingId = useRef(new Set());

  useEffect(() => {//reply click
    window.addEventListener("click", clickReply);
    return () => {
      window.removeEventListener("click", clickReply);
    }
  }, [])

  useEffect(() => {//初始化查詢文章id
    if (idList.length === 0) {
      forumRequest.getId().then(res => {
        dispatch(setAllId(res.idList));
      }).catch((e) => {
        dispatch(showConsole(i18next.t("findIdSet-err")));
      });
    }
  }, [])

  useEffect(() => {
    queryArticle();

    window.addEventListener("scroll", scrollDownQuery);
    return () => {
      window.removeEventListener("scroll", scrollDownQuery);
    }
  }, [queryIdList, queryPendingId])//???????????????????????????有需要?

  /* --- EventListener --- */
  const scrollDownQuery = (event) => {
    queryArticle();
  }

  const clickReply = (event) => {//關閉留言區
    if (replyIsOpen && !event.target.closest(`[${REPLY_BOX}]`)) {
      dispatch(replySetOpen(false));
    }
  }

  /* --- 其他 --- */
  const canQueryArticle = () => {
    if (!checkInBottom()) {
      return false;
    }

    
    if (queryIdList.length === 0) {
      console.log("Already query all articles, skip query");
      return false;
    }

    if (queryPendingId.current.size > 0) {
      console.log("Skip query articles because have pending query");
      return false;
    }

    return true;
  }

  const getQueryId = idList.filter(id => !forum.get(id)).slice(0, querySize);

  const queryArticle = () => {
    if (!canQueryArticle()) return;

    const queryIdList = idList.filter(id => !forum.get(id)).slice(0, querySize);
    if(ids.length === 0){

    }
    queryIdList.forEach(id => queryPendingId.add(id));
    queryIdList.forEach(id => httpGetArticle(id));
  }

  const httpGetArticle = () => {
    forumRequest.getArticle(id).then(article => {
      dispatch(setArticle(article));
    }).catch((e) => {
      dispatch(showConsole(i18next.t("findPost-err")));
    }).finally(() => {
      queryPendingId.delete(id);
      console.log("Query article finishe", id);
    });

  }

  useEffect(() => {
    queryLock.current = pendingId.size > 0;
  }, [pendingId])

  const checkInBottom = () => {
    const clientHeight = document.documentElement.clientHeight;
    const scrollTop = document.documentElement.scrollTop;
    const scrollHeight = document.documentElement.scrollHeight;
    return clientHeight + scrollTop + 100 > scrollHeight;
  }

  /* --- 頁面生成 --- */
  const createArt = () => {
    const allArt = [];
    for (let [id, a] of post) {
      if (!a) continue;//未讀取, 就跳過

      allArt.push(
        <div key={id} id={id} className="art">
          <ArtCont id={id} />
          <Fragment>{createCont(a.contList)}</Fragment>
          <Move id={id} />
          {id === replyId && <Reply id={id} />}
        </div>
      );
    }
    return allArt;
  }

  const createCont = (contList) => {
    const allCont = [];
    for (let i = 1; i < contList.length; ++i) {
      const c = contList[i];
      if (!c) continue;//未讀取, 就跳過

      const k = `${c.id}_${c.no}`;
      allCont.push(
        c.status === CONT_STATUS_TYPE.DELETED ?
          <ContDel key={k} id={c.id} no={c.no} /> :
          <Cont key={k} id={c.id} no={c.no} />
      );
    }
    return allCont;
  }

  return (
    <div>
      {createArt()}
    </div>
  )
}