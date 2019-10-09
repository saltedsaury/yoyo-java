package cn.idachain.finance.batch.task.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author kun
 * @version 2019/10/9 11:41
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ReconcileTaskTest {

    @Autowired
    private ReconcileTask reconcileTask;

    @Test
    public void testReconcile() {
        reconcileTask.reconcile();
    }
}