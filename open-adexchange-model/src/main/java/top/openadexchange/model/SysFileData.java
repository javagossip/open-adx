package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author weiping
 * @since 2025-12-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_file_data")
public class SysFileData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * file_object.id
     */
    @Id
    private Long fileId;

    /**
     * 文件二进制内容
     */
    private InputStream fileData;

}
