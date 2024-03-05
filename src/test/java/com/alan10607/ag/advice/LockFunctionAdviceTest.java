//package com.alan10607.ag.advice;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//class LockFunctionAdviceTest {
//
//    @Test
//    void executeWithLock() {
//    }
//
//
//    @Mock
//    private ProceedingJoinPoint pjp;
//
//    @Mock
//    private LockFunction lockFunction;
//
//    @InjectMocks
//    private LockFunctionAdvice lockFunctionAdvice;
//
//    @Test
//    public void testExecuteWithLock() throws Throwable {
//        // 设置 mock 的行为
//        when(pjp.proceed()).thenReturn("Result");
//
//        // 执行被测试的方法
//        Object result = lockFunctionAdvice.executeWithLock(pjp);
//
//        // 验证结果
//        assertEquals("Result", result);
//
//        // 验证 AOP 是否正确调用了 ProceedingJoinPoint 的 proceed 方法
//        verify(pjp, times(1)).proceed();
//    }
//
//    @Test
//    public void testGetFullFunctionName() throws Throwable {
//        // 设置 mock 的行为
//        Signature signature = mock(Signature.class);
//        when(signature.getDeclaringTypeName()).thenReturn("com.example.TestClass");
//        when(signature.getName()).thenReturn("testMethod");
//        when(pjp.getSignature()).thenReturn(signature);
//        when(pjp.getArgs()).thenReturn(new Object[]{1, "param"});
//
//        // 执行被测试的方法
//        String functionName = lockFunctionAdvice.getFullFunctionName(pjp);
//
//        // 验证结果
//        assertEquals("com.example.TestClass.testMethod(Integer,String)", functionName);
//    }
//}