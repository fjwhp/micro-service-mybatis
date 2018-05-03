package aljoin.aut.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 
 * 自定义remember me操作
 *
 * @author：zhongjy
 *
 * @date：2017年5月10日 下午9:28:06
 */
public class CustomJdbcTokenRepositoryImpl extends JdbcDaoSupport implements PersistentTokenRepository {
    public static final String CREATE_TABLE_SQL =
        "create table aut_remember_me (user_name varchar(64) not null, id varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)";
    public static final String DEF_TOKEN_BY_SERIES_SQL =
        "select user_name,id,token,last_used from aut_remember_me where id = ?";
    public static final String DEF_INSERT_TOKEN_SQL =
        "insert into aut_remember_me (user_name, id, token, last_used) values(?,?,?,?)";
    public static final String DEF_UPDATE_TOKEN_SQL =
        "update aut_remember_me set token = ?, last_used = ? where id = ?";
    public static final String DEF_REMOVE_USER_TOKENS_SQL = "delete from aut_remember_me where user_name = ?";
    private String tokensBySeriesSql = "select user_name,id,token,last_used from aut_remember_me where id = ?";
    private String insertTokenSql = "insert into aut_remember_me (user_name, id, token, last_used) values(?,?,?,?)";
    private String updateTokenSql = "update aut_remember_me set token = ?, last_used = ? where id = ?";
    private String removeUserTokensSql = "delete from aut_remember_me where user_name = ?";
    private boolean createTableOnStartup;

    @Override
    protected void initDao() {
        if (this.createTableOnStartup) {
            getJdbcTemplate().execute(
                "create table aut_remember_me (user_name varchar(64) not null, id varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)");
        }
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        getJdbcTemplate().update(this.insertTokenSql,
            new Object[] {token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate()});
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        getJdbcTemplate().update(this.updateTokenSql, new Object[] {tokenValue, lastUsed, series});
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return (PersistentRememberMeToken)getJdbcTemplate().queryForObject(this.tokensBySeriesSql,
                new RowMapper<Object>() {
                    @Override
                    public PersistentRememberMeToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getTimestamp(4));
                    }
                }, new Object[] {seriesId});
        } catch (EmptyResultDataAccessException zeroResults) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Querying token for series '" + seriesId + "' returned no results.", zeroResults);
            }
        } catch (IncorrectResultSizeDataAccessException moreThanOne) {
            this.logger.error(
                "Querying token for series '" + seriesId + "' returned more than one value. Series should be unique");
        } catch (DataAccessException e) {
            this.logger.error("Failed to load token for series " + seriesId, e);
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        getJdbcTemplate().update(this.removeUserTokensSql, new Object[] {username});
    }

    public void setCreateTableOnStartup(boolean createTableOnStartup) {
        this.createTableOnStartup = createTableOnStartup;
    }
}
