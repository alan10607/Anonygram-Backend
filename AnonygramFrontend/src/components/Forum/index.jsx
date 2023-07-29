import React from "react";
import Header from "./Header";
import Body from "./Body";
import New from "./New";
import Setting from "./Setting";
import Art from "./Art";

export default function Hub() {
  return (
    <div id="hub">
      <Header />
      <Body />
      <New />
      <Setting />
      <Art />
    </div>
  )
}