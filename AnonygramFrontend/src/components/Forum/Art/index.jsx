import { Fragment, useEffect, useRef } from 'react';
import { useDispatch, useSelector, shallowEqual } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { findIdSet, findPost } from '../../../redux/actions/post';
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

export default function Art() {
  const findPostLock = useRef(false);
  const findPostSize = 10;
  const { post, replyId, replyIsOpen } = useSelector(state => ({
    post: state.post,
    replyId: state.reply.id,
    replyIsOpen: state.reply.isOpen,
    userId: state.user.userId
  }), shallowEqual);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const idList = [...post.keys()];
  const findPostList = idList.filter(id => !post.get(id));

  const checkJwtValid = async () => {
    try {
      const valid = await authRequest.test();
      return true;
    } catch (e) {
      return false;
    }
  };

  const isJwtVaild = checkJwtValid();


  /* --- 初始化頁面 --- */
  useEffect(() => {//檢查Jwt
    if (!userId) {
      console.log("Jwt not found, navigate to login...");
      navigate("/login");
      return;
    }

    if (!isJwtVaild) {
      console.log(`Jwt was expired, navigate to login...`);
      navigate("/login");
      return;
    }
  }, [])

  useEffect(() => {//reply click
    window.addEventListener("click", clickReply);
    return () => {
      window.removeEventListener("click", clickReply);
    }
  }, [])

  useEffect(() => {//初始化查詢文章id
    if (isJwtVaild && idList.length === 0) {
      dispatch(findIdSet());
      console.log("Load id set");
    }
  }, [idList])

  useEffect(() => {//往下滑動找更多文章
    if (idList.length > 0 && findPostList.length > 0 && checkInBottom()) {
      doFindPost();//初始化時載入, 載入完後有仍有空位則繼續載入
    } else {
      findPostLock.current = false;
      // console.log("Load posts done");
    }

    window.addEventListener("scroll", scrollDown);
    return () => {
      window.removeEventListener("scroll", scrollDown);
    }
  }, [idList, findPostList])

  /* --- EventListener --- */
  const scrollDown = (event) => {
    if (!checkInBottom()) return;

    if (findPostList.length === 0) {
      console.log("Already find all arts, skip find art");
      return;
    }

    if (findPostLock.current) {
      console.log("Skip find art because findPostLock=true");
      return;
    }

    findPostLock.current = true;
    doFindPost();
  }

  const clickReply = (event) => {//關閉留言區
    if (replyIsOpen && !event.target.closest(`[${REPLY_BOX}]`)) {
      dispatch(replySetOpen(false));
    }
  }

  /* --- 其他 --- */
  const doFindPost = () => {
    console.log("Load posts from index", idList.indexOf(findPostList[0]));
    dispatch(findPost({
      idList: findPostList.slice(0, findPostSize)
    }));
  }

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