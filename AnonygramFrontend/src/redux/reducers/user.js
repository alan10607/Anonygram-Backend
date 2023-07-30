import { SET_USER, DELETE_USER } from "../actions/user";

const initUserState = {
  userId: null,
  username: null,
  isAnonymous: null
};

export default function userReducer(preState = initUserState, action) {
  const { type, data } = action;

  switch (type) {
    case SET_USER:
      const { userId, username, isAnonymous } = data;
      return Object.assign({}, preState, { userId, username, isAnonymous });

    case DELETE_USER:
      return initUserState;

    default:
      return preState;
  }
}